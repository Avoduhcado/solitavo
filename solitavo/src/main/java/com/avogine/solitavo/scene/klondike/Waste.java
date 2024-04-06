package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.klondike.entity.Card;

/**
 *
 */
public final class Waste implements CardStack {

	private final List<Card> cards;
	private final Vector2f position;
	
	/**
	 * 
	 */
	public Waste() {
		cards = new ArrayList<>();
		position = new Vector2f(72f, 0);
	}

	/**
	 * 
	 */
//	public void splayCards() {
//		getCards().forEach(card -> card.setPosition(new Vector2f(position)));
//		var sub = getCards().subList(Math.max(0, getCards().size() - 3), getCards().size());
//		for (int i = 0; i < sub.size(); i++) {
//			// TODO Magic number 12f
//			sub.get(i).setPosition(new Vector2f(position.x + i * 12f, position.y));
//		}
//	}

	@Override
	public boolean containsPoint(Vector2f point) {
		return !getCards().isEmpty() && getLast().getBoundingBox().containsPoint(point);
	}
	
	@Override
	public void clicked(Vector2f clickPosition, List<Card> cardsHeld, CardStack previous) {
		if (!cardsHeld.isEmpty()) {
			previous.reset(cardsHeld);
			return;
		}
		var wasteCard = removeLast();
		wasteCard.setClickOffset(clickPosition.x, clickPosition.y);
		cardsHeld.add(wasteCard);
	}
	
	@Override
	public void reset(List<Card> cards) {
		// cards should only ever be size == 1
//		if (cards.size() != 1) {
//			throw new IllegalArgumentException("Only 1 card should ever be taken from the Waste at a time.");
//		}
//		cards.getFirst().setPosition(new Vector2f(isEmpty() ? position.x : getLast().getPosition().x + 12f, 0f));
//		addAll(cards);
//		cards.clear();
	}
	
	@Override
	public void cardTaken() {
//		splayCards();
	}
	
	@Override
	public List<Card> getCards() {
		return cards;
	}

	@Override
	public void addCard(Card card) {
		if (getCards().size() > 2) {
			for (int i = getCards().size() - 1; i > Math.max(0, getCards().size() - 3); i--) {
				getCards().get(i).setPosition(new Vector2f(position.x + i * 12f, position.y));
			}
		}
		if (getLast() != null) {
			card.setPosition(new Vector2f(getLast().getPosition().x + 12f, position.y));
		} else {
			card.setPosition(new Vector2f(position));
		}
		card.setFaceUp(true);
		getCards().add(card);
	}

	@Override
	public void addCards(List<Card> cardList) {
		cardList.forEach(this::addCard);
	}
	
	@Override
	public boolean removeCard(Card card) {
		var removed = CardStack.super.removeCard(card);
		if (getCards().size() > 2) {
			for (int i = getCards().size() - 1; i > Math.max(0, getCards().size() - 3); i--) {
				getCards().get(i).setPosition(new Vector2f(position.x + i * 12f, position.y));
			}
		}
		return removed;
	}
	
	@Override
	public void removeCards(List<Card> cardList) {
		cardList.forEach(this::removeCard);
	}

	@Override
	public Rectanglef getBounds() {
		return null;
	}

}
