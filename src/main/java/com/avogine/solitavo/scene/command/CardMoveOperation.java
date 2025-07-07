package com.avogine.solitavo.scene.command;

import java.util.List;

import com.avogine.logging.AvoLog;
import com.avogine.solitavo.scene.cards.Card;
import com.avogine.solitavo.scene.klondike.Pile;
import com.avogine.solitavo.scene.util.CardHolder;

/**
 *
 */
public class CardMoveOperation implements CardOperation {

	protected List<Card> cards;
	
	protected CardHolder origin;
	protected CardHolder destination;
	
	protected boolean reverseOrdering;
	
	protected boolean unreveal;
	
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
	public void execute(float delta) {
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
		AvoLog.log().debug("Move cards {} from origin: `{}` to destination: `{}` in reverse: [{}]", cards, origin, destination, reverseOrdering);
	}

	@Override
	public boolean isExecuting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		
	}
}
