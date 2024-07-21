package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.logging.AvoLog;
import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.render.SpriteRender;
import com.avogine.solitavo.scene.cards.Card;
import com.avogine.solitavo.scene.command.*;
import com.avogine.solitavo.scene.util.CardHolder;
import com.avogine.util.Pair;

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
	 * 
	 */
	public void init() {
		cards.clear();
		supplier = null;
		updateBoundingBox();
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
		updateBoundingBox();
	}
	
	/**
	 * @param cardAndSupplierPair
	 */
	public void holdCard(Pair<Card, CardHolder> cardAndSupplierPair) {
		holdCard(cardAndSupplierPair.first(), cardAndSupplierPair.second());
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
	 * @param cardsAndSupplierPair
	 */
	public void holdCards(Pair<List<Card>, CardHolder> cardsAndSupplierPair) {
		holdCards(cardsAndSupplierPair.first(), cardsAndSupplierPair.second());
	}
	
	/**
	 * 
	 */
	public void removeCards() {
		cards.forEach(Card::setPosition);
		cards.clear();
		supplier = null;
		updateBoundingBox();
	}
	
	/**
	 * @param consumer
	 * @return 
	 */
	public CardOperation placeCards(CardHolder consumer) {
		var moveOperation = new CardMoveOperation(getCards(), supplier, consumer);
		cards.clear();
		updateBoundingBox();
		return moveOperation;
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void move(float x, float y) {
		cards.keySet().forEach(card -> card.getPosition().add(x, y));
		updateBoundingBox();
	}
	
	private void updateBoundingBox() {
		var cardList = getCards();
		if (cardList.isEmpty()) {
			boundingBox.setMin(0, 0).setMax(0, 0);
		} else {
			var lastCard = cardList.getLast();
			boundingBox.setMin(cardList.getFirst().getPosition())
			.setMax(lastCard.getPosition().x + lastCard.getSize().x, lastCard.getPosition().y + lastCard.getSize().y);
		}
	}
	
	/**
	 * @param render
	 * @param texture
	 */
	public void render(SpriteRender render, TextureAtlas texture) {
		cards.keySet().forEach(card -> render.renderSpriteAtlas(card.getPosition(), card.getSize(), texture, card.getRank().ordinal(), card.getSuit().ordinal()));
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
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
	
	/**
	 * @return
	 */
	public CardHolder getSupplier() {
		return supplier;
	}
	
}
