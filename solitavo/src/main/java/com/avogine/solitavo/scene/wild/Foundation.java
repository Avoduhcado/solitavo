package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.*;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.util.*;

/**
 *
 */
public class Foundation implements CardConsumer, CardSupplier {
	
	private final List<Card> stack;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	private final Vector4f blankCardOffset;
	
	/**
	 * @param index 
	 */
	public Foundation(int index) {
		stack = new ArrayList<>();
		position = new Vector2f((72f * 3) + index * 72f, 0f);
		size = new Vector2f(72f, 100f);
		boundingBox = new Rectanglef(position, position.add(size, new Vector2f()));
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
	 * @return
	 */
	public Optional<Card> getTopCard() {
		if (stack.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(stack.getLast());
	}
	
	/**
	 * @param renderer
	 * @param texture
	 */
	public void draw(SpriteRenderer renderer, TextureAtlas texture) {
		if (!stack.isEmpty()) {
			if (stack.size() == 1) {
				renderer.drawSprite(position, size, texture.getId(), blankCardOffset);
			} else {
				var secondTopCard = stack.get(stack.size() - 2);
				renderer.drawSprite(secondTopCard.getPosition(), secondTopCard.getSize(), texture.getId(), secondTopCard.computeTextureOffset(texture));
			}
		}
		getTopCard().ifPresentOrElse(
				topCard -> renderer.drawSprite(topCard.getPosition(), topCard.getSize(), texture.getId(), topCard.computeTextureOffset(texture)),
				() -> renderer.drawSprite(position, size, texture.getId(), blankCardOffset));
	}
	
	@Override
	public boolean canStack(List<Card> cards) {
		if (cards.size() != 1) {
			return false;
		} else if (stack.isEmpty()) {
			return cards.getFirst().getRank() == Rank.ACE;
		} else {
			Card topCard = stack.getLast();
			Card heldCard = cards.getFirst(); 
			return heldCard.getSuit() == topCard.getSuit() && heldCard.getRank().ordinal() - topCard.getRank().ordinal() == 1;
		}
	}
	
	/**
	 * @return the boundingBox
	 */
	public Rectanglef getBoundingBox() {
		return boundingBox;
	}

}
