package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.util.CardStack;

/**
 *
 */
public class Pile implements CardStack {

	private final List<Card> cards;
	
	private final Vector2f position;
	
	private final Rectanglef boundingBox;
	
	private final Rectanglef faceUpBounds;

	private final float faceDownOffset;
	private final float faceUpOffset;
	
	/**
	 * @param position 
	 * @param size 
	 */
	public Pile(Vector2f position, Vector2f size) {
		cards = new ArrayList<>();
		this.position = position;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.y + size.y);
		faceUpBounds = new Rectanglef(boundingBox);
		faceDownOffset = size.y * 0.12f;
		faceUpOffset = size.y * 0.2f;
	}
	
	/**
	 * @param card
	 */
	public void dealCard(Card card) {
		cards.add(card);
		card.setPosition(position.x, position.y + (faceDownOffset * cards.indexOf(card)));
	}
	
	/**
	 * @param faceUp 
	 */
	public void revealTopCard(boolean faceUp) {
		if (isEmpty()) {
			return;
		}
		cards.getLast().setFaceUp(faceUp);
		updateFaceUpBounds();
	}
	
	/**
	 * 
	 */
	public void revealTopCard() {
		revealTopCard(true);
	}
	
	@Override
	public void addCards(List<Card> cards) {
		cards.forEach(card -> {
			float verticalOffset = getVerticalOffset();
			this.cards.add(card);
			card.setPosition(position.x, verticalOffset);
		});
		updateFaceUpBounds();
	}
	
	@Override
	public List<Card> removeCards(List<Card> cards) {
		this.cards.removeAll(cards);
		revealTopCard();
		return cards;
	}
	
	private void updateFaceUpBounds() {
		cards.stream()
				.dropWhile(card -> !card.isFaceUp())
				.findFirst()
				.ifPresentOrElse(card -> faceUpBounds.setMin(card.getPosition()), () -> faceUpBounds.setMin(position));
		if (isEmpty()) {
			faceUpBounds.setMax(boundingBox.maxX, boundingBox.maxY);
		} else {
			faceUpBounds.setMax(cards.getLast().getBoundingBox().maxX, cards.getLast().getBoundingBox().maxY);
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Card> getCardsFromPoint(float x, float y) {
		List<Card> cascade = new ArrayList<>();
		cards.reversed().stream()
		.filter(card -> card.isFaceUp() && card.getBoundingBox().containsPoint(x, y))
		.findFirst()
		.ifPresent(card -> cascade.addAll(cards.subList(cards.indexOf(card), cards.size())));
		
		return cascade;
	}
	
	/**
	 * @param point
	 * @return
	 */
	public List<Card> getCardsFromPoint(Vector2f point) {
		return getCardsFromPoint(point.x, point.y);
	}
	
	@Override
	public boolean canStack(List<Card> cards) {
		if (isEmpty() && cards.getFirst().getRank() == Rank.KING) {
			return true;
		} else if (!isEmpty()) {
			Card topCard = this.cards.getLast();
			Card firstHeldCard = cards.getFirst();
			return Suit.isOpposite(topCard.getSuit(), firstHeldCard.getSuit()) && topCard.getRank().ordinal() - firstHeldCard.getRank().ordinal() == 1;
		}
		return false;
	}
	
	/**
	 * @param renderer
	 * @param texture
	 */
	public void render(SpriteRenderer renderer, TextureAtlas texture) {
		if (isEmpty()) {
			return;
		}
		cards.forEach(card -> renderer.renderSprite(card.getPosition(), card.getSize(), texture.getId(), card.computeTextureOffset(texture)));
	}
	
	private float getVerticalOffset() {
		var previousCard = isEmpty() ? null : cards.getLast();
		if (previousCard == null) {
			return position.y;
		} else if (previousCard.isFaceUp()) {
			return previousCard.getPosition().y + faceUpOffset;
		} else {
			return previousCard.getPosition().y + faceDownOffset;
		}
	}
	
	/**
	 * @param card
	 * @return
	 */
	public int getIndexOf(Card card) {
		return cards.indexOf(card);
	}
	
	/**
	 * @param index
	 * @return
	 */
	public boolean isCardFaceUpAtIndex(int index) {
		return cards.get(index).isFaceUp();
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	/**
	 * Returns specifically the union of the bounds of the face up cards in this pile when the pile is not empty.
	 */
	@Override
	public Rectanglef getBoundingBox() {
		if (isEmpty()) {
			return boundingBox;
		} else {
			return faceUpBounds;
		}
	}

}
