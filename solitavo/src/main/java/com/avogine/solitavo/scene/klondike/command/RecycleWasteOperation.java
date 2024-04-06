package com.avogine.solitavo.scene.klondike.command;

import com.avogine.solitavo.scene.klondike.*;

/**
 * @param stock 
 * @param waste 
 *
 */
public record RecycleWasteOperation(Stock stock, Waste waste) implements CardOperation {

	@Override
	public void execute() {
		stock.addCards(waste.getCards());
		stock.getCards().forEach(card -> card.setFaceUp(false));
		waste.getCards().clear();
	}

	@Override
	public void rollback() {
		waste.addCards(stock.getCards());
		waste.getCards().forEach(card -> card.setFaceUp(true));
		stock.getCards().clear();
	}

}
