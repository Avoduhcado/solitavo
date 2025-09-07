package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.cards.Card;
import com.avogine.solitavo.scene.command.*;
import com.avogine.solitavo.scene.util.CardHolder;
import com.avogine.util.Pair;

/**
 *
 */
public class Hand {

	private final List<Card> cards;
	private final List<Vector2f> initialPositions;
	
	private CardHolder supplier;
	
	private final Rectanglef boundingBox;
	
	/**
	 * 
	 */
	public Hand() {
		cards = new ArrayList<>();
		initialPositions = new ArrayList<>();
		boundingBox = new Rectanglef();
	}
	
	/**
	 * 
	 */
	public void init() {
		cards.clear();
		initialPositions.clear();
		supplier = null;
		updateBoundingBox();
	}
	
	/**
	 * @param card
	 * @param supplier 
	 */
	public void holdCard(Card card, CardHolder supplier) {
		this.supplier = supplier;
		cards.add(card);
		initialPositions.add(new Vector2f().set(card.getPosition()));
		card.setSelected(true);
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
		cards.forEach(card -> {
			var initialPosition = initialPositions.get(cards.indexOf(card));
			card.setPosition(initialPosition);
			card.setSelected(false);
		});
		init();
	}
	
	/**
	 * @param consumer
	 * @return 
	 */
	public CardOperation placeCards(CardHolder consumer) {
		var moveOperation = new CardAnimatedMoveOperation(getCards(), supplier, consumer);
		init();
		return moveOperation;
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void move(float x, float y) {
		cards.forEach(card -> card.getPosition().add(x, y));
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
		return cards;
	}
	
	/**
	 * @return
	 */
	public CardHolder getSupplier() {
		return supplier;
	}
	
	@Override
	public String toString() {
		return "Hand";
	}
	
}
