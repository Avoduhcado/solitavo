package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.klondike.entity.*;

/**
 *
 */
public final class Foundation extends ArrayList<Card> implements CardStack {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Rectanglef bounds;
	
	/**
	 * @param offset 
	 * 
	 */
	public Foundation(Vector2f offset) {
		bounds = new Rectanglef(offset, offset.add(72f, 100f, new Vector2f()));
	}
	
	/**
	 * @return the bounds
	 */
	public Rectanglef getBounds() {
		return bounds;
	}
	
	@Override
	public boolean containsPoint(Vector2f point) {
		return getBounds().containsPoint(point);
	}

	@Override
	public void clicked(Vector2f clickPosition, List<Card> cardsHeld, CardStack previous) {
		if (cardsHeld.isEmpty()) {
			var foundationCard = removeLast();
			foundationCard.setClickOffset(clickPosition.x, clickPosition.y);
			cardsHeld.add(foundationCard);
		} else {
			if (isStackable(cardsHeld)) {
				add(cardsHeld.getFirst());
				cardsHeld.clear();
				previous.cardTaken();
			} else {
				previous.reset(cardsHeld);
			}
		}
	}
	
	@Override
	public void reset(List<Card> cards) {
		addAll(cards);
		cards.forEach(card -> card.setPosition(new Vector2f(bounds.minX, bounds.minY)));
		cards.clear();
	}

	public boolean isStackable(List<Card> cards) {
		if (cards.size() > 1) {
			return false;
		}
		
		return shouldStartNewStack(cards.getFirst()) || shouldAddToStack(cards.getFirst());
	}
	
	private boolean shouldStartNewStack(Card card) {
		return isEmpty() && card.getRank() == Rank.ACE.ordinal();
	}
	
	private boolean shouldAddToStack(Card card) {
		return !isEmpty() && card.getSuit() == getFirst().getSuit() && card.getRank() == getLast().getRank() + 1;
	}

	@Override
	public List<Card> getCards() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCard(Card card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCards(List<Card> cardList) {
		// TODO Auto-generated method stub
		
	}

}
