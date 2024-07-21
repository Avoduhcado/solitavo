package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.render.SpriteRender;
import com.avogine.solitavo.scene.cards.*;
import com.avogine.solitavo.scene.util.CardStack;

/**
 *
 */
public class Foundation implements CardStack {
	
	private final List<Card> stack;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	/**
	 * @param position 
	 * @param size 
	 */
	public Foundation(Vector2f position, Vector2f size) {
		stack = new ArrayList<>();
		this.position = position;
		this.size = size;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.y + size.y);
	}
	
	/**
	 * 
	 */
	public void init() {
		stack.clear();
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
	public void render(SpriteRender renderer, TextureAtlas texture) {
		renderer.renderSpriteAtlas(position, size, texture, Rank.KING.ordinal(), Suit.BONUS.ordinal());
		if (!isEmpty() && stack.size() > 1) {
			var secondTopCard = stack.get(stack.size() - 2);
			renderer.renderSpriteAtlas(secondTopCard.getPosition(), secondTopCard.getSize(), texture, secondTopCard.getRank().ordinal(), secondTopCard.getSuit().ordinal());
		}
		getTopCard().ifPresent(topCard -> renderer.renderSpriteAtlas(topCard.getPosition(), topCard.getSize(), texture, topCard.getRank().ordinal(), topCard.getSuit().ordinal()));
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
