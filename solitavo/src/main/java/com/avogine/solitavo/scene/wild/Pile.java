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
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	private final Rectanglef faceUpBounds;
	
	/**
	 * @param index 
	 */
	public Pile(int index) {
		cards = new ArrayList<>();
		position = new Vector2f(index * 72f, 100f);
		size = new Vector2f(72f, 100f);
		boundingBox = new Rectanglef(position, position.add(size, new Vector2f()));
		faceUpBounds = new Rectanglef(boundingBox);
	}
	
	/**
	 * @param card
	 */
	public void dealCard(Card card) {
		cards.add(card);
		card.setPosition(position.x, position.y + (12.5f * cards.indexOf(card)));
	}
	
	/**
	 * 
	 */
	public void revealTopCard() {
		if (cards.isEmpty()) {
			return;
		}
		cards.getLast().setFaceUp(true);
		updateFaceUpBounds();
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
		if (cards.isEmpty()) {
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
	

	@Override
	public boolean canStack(List<Card> cards) {
		if (this.cards.isEmpty() && cards.getFirst().getRank() == Rank.KING) {
			return true;
		} else if (!this.cards.isEmpty()) {
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
	public void draw(SpriteRenderer renderer, TextureAtlas texture) {
		if (cards.isEmpty()) {
			return;
		}
		cards.forEach(card -> renderer.drawSprite(card.getPosition(), card.getSize(), texture.getId(), card.computeTextureOffset(texture)));
	}
	
	private float getVerticalOffset() {
		var previousCard = cards.isEmpty() ? null : cards.getLast();
		if (previousCard == null) {
			return position.y;
		} else if (previousCard.isFaceUp()) {
			return previousCard.getPosition().y + 20f;
		} else {
			return previousCard.getPosition().y + 12.5f;
		}
		
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	/**
	 * @return the boundingBox
	 */
	public Rectanglef getBoundingBox() {
		if (cards.isEmpty()) {
			return boundingBox;
		} else {
			return faceUpBounds;
		}
	}

}
