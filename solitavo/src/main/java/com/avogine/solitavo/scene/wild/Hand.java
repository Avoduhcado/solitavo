package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.Vector2f;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.Card;
import com.avogine.solitavo.scene.wild.util.*;

/**
 *
 */
public class Hand {

	private final Map<Card, Vector2f> cards;
	
	private CardSupplier supplier;
	
	/**
	 * 
	 */
	public Hand() {
		cards = new LinkedHashMap<>();
	}
	
	/**
	 * @param card
	 * @param supplier 
	 */
	public void holdCard(Card card, CardSupplier supplier) {
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
	public void holdCards(List<Card> cards, CardSupplier supplier) {
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
	 */
	public void placeCards(CardConsumer consumer) {
		consumer.addCards(supplier.removeCards(getCards()));
		cards.clear();
		supplier = null;
	}
	
	/**
	 * @param cards
	 * @param supplier 
	 * @param consumers 
	 */
	public void autoPlaceCard(List<Card> cards, CardSupplier supplier, List<CardConsumer> consumers) {
		consumers.stream()
		.filter(consumer -> consumer.canStack(cards))
		.findFirst()
		.ifPresent(consumer -> consumer.addCards(supplier.removeCards(cards)));
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void moveCards(float x, float y) {
		cards.keySet().forEach(card -> card.getPosition().add(x, y));
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
	public List<Card> getCards() {
		return cards.keySet().stream().toList();
	}
	
}
