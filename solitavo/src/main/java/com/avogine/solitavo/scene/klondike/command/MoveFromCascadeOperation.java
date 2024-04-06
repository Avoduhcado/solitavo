package com.avogine.solitavo.scene.klondike.command;

import java.util.List;

import com.avogine.solitavo.scene.klondike.*;
import com.avogine.solitavo.scene.klondike.entity.Card;

/**
 * @param cards 
 * @param origin 
 * @param destination 
 * @param revealNextCard 
 */
public record MoveFromCascadeOperation(List<Card> cards, Cascade origin, CardStack destination, boolean revealNextCard) implements CardOperation {

	@Override
	public void execute() {
		origin.getCards().removeAll(cards);
		destination.addCards(cards);
		if (revealNextCard && origin.getLast() != null) {
			origin.getLast().setFaceUp(true);
		}
	}

	@Override
	public void rollback() {
		if (revealNextCard && origin.getLast() != null) {
			origin.getLast().setFaceUp(false);
		}
		destination.getCards().removeAll(cards);
		origin.addCards(cards);
	}

}
