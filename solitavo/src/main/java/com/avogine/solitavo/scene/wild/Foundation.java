package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.*;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.util.CardStack;

/**
 *
 */
public class Foundation implements CardStack {
	
	private final List<Card> stack;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	private final Vector4f blankCardOffset;
	
	/**
	 * @param position 
	 * @param size 
	 */
	public Foundation(Vector2f position, Vector2f size) {
		stack = new ArrayList<>();
		this.position = position;
		this.size = size;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.y + size.y);
		blankCardOffset = new Vector4f(
				(float) Rank.KING.ordinal() / Rank.values().length,
				(float) Suit.BONUS.ordinal() / Suit.values().length,
				1f / Rank.values().length, 1f / Suit.values().length);
	}
	
	/**
	 * @param card
	 */
	public void addCard(Card card) {
		stack.add(card);
		card.setPosition(position);
	}
	
	@Override
	public void addCards(List<Card> cards) {
		addCard(cards.getFirst());
	}
	
	@Override
	public List<Card> removeCards(List<Card> cards) {
		this.stack.removeAll(cards);
		return cards;
	}
	
	/**
	 * @return the {@link Card} at the top of this stack if this stack is not empty.
	 */
	public Optional<Card> getTopCard() {
		if (isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(stack.getLast());
	}
	
	/**
	 * @param renderer
	 * @param texture
	 */
	public void render(SpriteRenderer renderer, TextureAtlas texture) {
		if (!isEmpty()) {
			if (stack.size() == 1) {
				renderer.renderSprite(position, size, texture.getId(), blankCardOffset);
			} else {
				var secondTopCard = stack.get(stack.size() - 2);
				renderer.renderSprite(secondTopCard.getPosition(), secondTopCard.getSize(), texture.getId(), secondTopCard.computeTextureOffset(texture));
			}
		}
		getTopCard().ifPresentOrElse(
				topCard -> renderer.renderSprite(topCard.getPosition(), topCard.getSize(), texture.getId(), topCard.computeTextureOffset(texture)),
				() -> renderer.renderSprite(position, size, texture.getId(), blankCardOffset));
	}
	
	@Override
	public boolean canStack(List<Card> cards) {
		if (cards.size() != 1) {
			return false;
		} else if (isEmpty()) {
			return cards.getFirst().getRank() == Rank.ACE;
		} else {
			Card topCard = stack.getLast();
			Card heldCard = cards.getFirst(); 
			return heldCard.getSuit() == topCard.getSuit() && heldCard.getRank().ordinal() - topCard.getRank().ordinal() == 1;
		}
	}
	
	/**
	 * @return True if there are no {@link Card}s in this stack.
	 */
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	@Override
	public Rectanglef getBoundingBox() {
		return boundingBox;
	}

}
