package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.klondike.command.*;
import com.avogine.solitavo.scene.klondike.entity.Card;

/**
 *
 */
public final class Stock implements CardStack {

	private final List<Card> cards;
	private final Rectanglef bounds;
	
	private final Waste waste;
	
	private int drawCount;
	
	private KlondikeScene scene;
	
	/**
	 * @param position 
	 * @param waste 
	 * @param drawCount 
	 * 
	 */
	public Stock(Vector2f position, Waste waste, int drawCount, KlondikeScene scene) {
		cards = new ArrayList<>();
		bounds = new Rectanglef(position, KlondikeScene.CARD_SIZE);
		this.waste = waste;
		this.drawCount = drawCount;
		this.scene = scene;
	}
	
	/**
	 * @return the bounds
	 */
	public Rectanglef getBounds() {
		return bounds;
	}
	
//	@Override
//	public boolean containsPoint(Vector2f point) {
//		return getBounds().containsPoint(point);
//	}

	@Override
	public void clicked(Vector2f clickPosition, List<Card> cardsHeld, CardStack previous) {
		if (!cardsHeld.isEmpty()) {
			previous.reset(cardsHeld);
			return;
		}
		if (getCards().isEmpty()) {
			scene.executeOperation(new RecycleWasteOperation(this, waste));
//			addAll(waste);
//			waste.forEach(card -> card.setFaceUp(false));
//			waste.clear();
		} else {
			scene.executeOperation(new DrawFromStockOperation(this, this.getCards().size(), waste));
//			var stockList = subList(0, Math.min(drawCount, size()));
//			stockList.forEach(card -> card.setFaceUp(true));
//			waste.addAll(stockList);
//			waste.splayCards();
//			removeAll(stockList);
		}
	}
	
	@Override
	public void reset(List<Card> cards) {
		// Not implemented
	}

	@Override
	public List<Card> getCards() {
		return cards;
	}
	
	/**
	 * @return the drawCount
	 */
	public int getDrawCount() {
		return drawCount;
	}

	@Override
	public void addCard(Card card) {
		card.setFaceUp(false);
		card.setPosition(new Vector2f(bounds.minX, bounds.minY));
		cards.add(card);
	}

	@Override
	public void addCards(List<Card> cardList) {
		cardList.forEach(this::addCard);
	}

}
