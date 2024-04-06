package com.avogine.solitavo.scene.klondike.command;

import org.joml.Math;

import com.avogine.solitavo.scene.klondike.*;

/**
 * @param stock 
 * @param stockSize 
 * @param waste 
 *
 */
public record DrawFromStockOperation(Stock stock, int stockSize, Waste waste) implements CardOperation {

	@Override
	public void execute() {
		var stockCards = stock.getCards().subList(0, Math.min(stock.getDrawCount(), stockSize));
		stockCards.forEach(card -> card.setFaceUp(true));
		waste.addCards(stockCards);
		stock.getCards().removeAll(stockCards);
	}

	@Override
	public void rollback() {
		var wasteCards = waste.getCards().subList(waste.getCards().size() - stockSize, waste.getCards().size());
		wasteCards.forEach(card -> card.setFaceUp(false));
		stock.addCards(wasteCards);
		waste.getCards().removeAll(wasteCards);
	}

}
