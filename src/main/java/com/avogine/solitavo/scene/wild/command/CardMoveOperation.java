package com.avogine.solitavo.scene.wild.command;

import java.util.List;

import com.avogine.logging.AvoLog;
import com.avogine.solitavo.scene.wild.Pile;
import com.avogine.solitavo.scene.wild.cards.Card;
import com.avogine.solitavo.scene.wild.util.CardHolder;

/**
 *
 */
public class CardMoveOperation implements CardOperation {

	private List<Card> cards;
	
	private CardHolder origin;
	private CardHolder destination;
	
	private boolean reverseOrdering;
	
	private boolean unreveal;
	
	/**
	 * @param cards 
	 * @param origin 
	 * @param destination 
	 * @param reverseOrdering 
	 */
	public CardMoveOperation(List<Card> cards, CardHolder origin, CardHolder destination, boolean reverseOrdering) {
		this.cards = List.copyOf(cards);
		this.origin = origin;
		this.destination = destination;
		this.reverseOrdering = reverseOrdering;
	}
	
	/**
	 * @param cards 
	 * @param origin 
	 * @param destination 
	 * 
	 */
	public CardMoveOperation(List<Card> cards, CardHolder origin, CardHolder destination) {
		this(cards, origin, destination, false);
	}
	
	@Override
	public void execute() {
		if (origin instanceof Pile pile && !pile.isEmpty()) {
			int selectionIndex = pile.getIndexOf(cards.getFirst());
			unreveal = selectionIndex > 0 && !pile.isCardFaceUpAtIndex(selectionIndex - 1);
		}
		destination.addCards(origin.removeCards(cards));
	}

	@Override
	public void rollback() {
		if (reverseOrdering) {
			origin.addCards(destination.removeCards(cards.reversed()));
		} else {
			if (origin instanceof Pile pile && unreveal) {
				pile.revealTopCard(false);
			}
			origin.addCards(destination.removeCards(cards));
		}
	}

	@Override
	public boolean incrementsMoves() {
		return true;
	}
	
	@Override
	public void describe() {
		AvoLog.log().debug("Move cards {} from origin: {} to destination: {} in reverse? {}", cards, origin, destination, reverseOrdering);
	}

}
