package com.avogine.solitavo.scene.klondike.command;

import java.util.List;

import com.avogine.solitavo.scene.klondike.CardStack;
import com.avogine.solitavo.scene.klondike.entity.Card;

/**
 *
 */
public class CardMoveOperation implements CardOperation {

	private List<Card> cards;
	
	private CardStack origin;
	private CardStack destination;
	
	/**
	 * @param cards 
	 * @param origin 
	 * @param destination 
	 * 
	 */
	public CardMoveOperation(List<Card> cards, CardStack origin, CardStack destination) {
		this.cards = cards;
		this.origin = origin;
		this.destination = destination;
	}
	
	@Override
	public void execute() {
		origin.getCards().removeAll(cards);
		destination.addCards(cards);
	}

	@Override
	public void rollback() {
		destination.getCards().removeAll(cards);
		origin.addCards(cards);
	}

}
