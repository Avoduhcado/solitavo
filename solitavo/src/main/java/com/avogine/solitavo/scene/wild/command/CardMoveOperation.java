package com.avogine.solitavo.scene.wild.command;

import java.util.List;

import com.avogine.solitavo.scene.wild.cards.Card;
import com.avogine.solitavo.scene.wild.util.CardHolder;

/**
 *
 */
public class CardMoveOperation implements CardOperation {

	private List<Card> cards;
	
	private CardHolder origin;
	private CardHolder destination;
	
	/**
	 * @param cards 
	 * @param origin 
	 * @param destination 
	 * 
	 */
	public CardMoveOperation(List<Card> cards, CardHolder origin, CardHolder destination) {
		this.cards = cards;
		this.origin = origin;
		this.destination = destination;
	}
	
	@Override
	public void execute() {
		destination.addCards(origin.removeCards(cards));
	}

	@Override
	public void rollback() {
		// TODO
//		destination.getCards().removeAll(cards);
//		origin.addCards(cards);
	}

}
