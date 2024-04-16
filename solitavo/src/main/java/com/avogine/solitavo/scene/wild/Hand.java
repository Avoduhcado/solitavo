package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.Card;
import com.avogine.solitavo.scene.wild.command.*;
import com.avogine.solitavo.scene.wild.util.*;

/**
 *
 */
public class Hand {

	private final Map<Card, Vector2f> cards;
	
	private CardHolder supplier;
	
	private final Rectanglef boundingBox;
	
	/**
	 * 
	 */
	public Hand() {
		cards = new LinkedHashMap<>();
		boundingBox = new Rectanglef();
	}
	
	/**
	 * @param card
	 * @param supplier 
	 */
	public void holdCard(Card card, CardHolder supplier) {
		this.supplier = supplier;
		try {
			this.cards.put(card, (Vector2f) card.getPosition().clone());
		} catch (CloneNotSupportedException e) {
			AvoLog.log().error("Failed to clone card position.", e);
		}
	}
	
	/**
	 * @param cards
	 * @param supplier 
	 */
	public void holdCards(List<Card> cards, CardHolder supplier) {
		for (Card card : cards) {
			holdCard(card, supplier);
		}
	}
	
	/**
	 * 
	 */
	public void removeCards() {
		cards.forEach(Card::setPosition);
		cards.clear();
		supplier = null;
	}
	
	/**
	 * @param consumer
	 * @return 
	 */
	public CardOperation placeCards(CardHolder consumer) {
		var moveOperation = new CardMoveOperation(getCards(), supplier, consumer);
		cards.clear();
		return moveOperation;
	}
	
	/**
	 * @param cards
	 * @param consumers 
	 * @return 
	 */
	public Optional<CardStack> autoPlaceCard(List<Card> cards, List<CardStack> consumers) {
		return consumers.stream()
		.filter(consumer -> consumer.canStack(cards))
		.findFirst();
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void moveCards(float x, float y) {
		cards.keySet().forEach(card -> card.getPosition().add(x, y));
		var cardArray = cards.keySet().toArray(new Card[0]);
		if (cardArray.length == 0) {
			return;
		}
		var lastCard = cardArray[cardArray.length - 1];
		boundingBox.setMin(cardArray[0].getPosition())
		.setMax(lastCard.getPosition().x + lastCard.getSize().x, lastCard.getPosition().y + lastCard.getSize().y);
	}
	
	/**
	 * @param render
	 * @param texture
	 */
	public void draw(SpriteRenderer render, TextureAtlas texture) {
		cards.keySet().forEach(card -> render.drawSprite(card.getPosition(), card.getSize(), texture.getId(), card.computeTextureOffset(texture)));
	}
	
	/**
	 * @return
	 */
	public boolean isHolding() {
		return !cards.isEmpty();
	}
	
	/**
	 * @return
	 */
	public int getHeldCount() {
		return cards.size();
	}
	
	/**
	 * @return
	 */
	public Rectanglef getBoundingBox() {
		return boundingBox;
	}
	
	/**
	 * @return
	 */
	public List<Card> getCards() {
		return cards.keySet().stream().toList();
	}
	
}
