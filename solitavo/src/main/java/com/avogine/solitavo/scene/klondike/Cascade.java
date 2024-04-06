package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.klondike.command.*;
import com.avogine.solitavo.scene.klondike.entity.*;

/**
 *
 */
public final class Cascade implements CardStack {

	private static final float FACE_UP_OFFSET = 5f;
	private static final float FACE_DOWN_OFFSET = 8f;
	
	private final List<Card> cards;
	private final Rectanglef emptyBounds;
	private final Rectanglef cascadeBounds;
	
	// TODO Register or remove
	private final KlondikeScene scene;
	
	/**
	 * @param offset 
	 * 
	 */
	public Cascade(Vector2f offset, KlondikeScene scene) {
		cards = new ArrayList<>();
		emptyBounds = new Rectanglef(offset, offset.add(KlondikeScene.CARD_SIZE, new Vector2f()));
		cascadeBounds = new Rectanglef(emptyBounds);
		this.scene = scene;
	}
	
	@Override
	public List<Card> getCards() {
		return cards;
	}
	
	@Override
	public void addCard(Card card) {
		card.setPosition(new Vector2f(
				emptyBounds.minX,
				getCards().isEmpty() ? emptyBounds.minY : getLast().getPosition().y + getNextVerticalOffset()));
//				getCards().isEmpty() ? emptyBounds.minY : getLast().getBoundingBox().minY + getCardVerticalOffset(getCards().size() - 1)));
		getCards().add(card);
		resizeBounds();
	}
	
	@Override
	public void addCards(List<Card> cardList) {
		cardList.forEach(this::addCard);
//		for (int i = 0; i < size(); i++) {
//			get(i).setPosition(new Vector2f(
//					bounds.minX,
//					i == 0 ? bounds.minY : get(i - 1).getBoundingBox().minY + getCardVerticalOffset(i - 1)));
//		}
	}
	
	@Override
	public Card remove(int index) {
		var card = CardStack.super.remove(index);
		resizeBounds();
		return card;
	}
	
	@Override
	public boolean removeCard(Card card) {
		var wasRemoved = CardStack.super.removeCard(card);
		resizeBounds();
		return wasRemoved;
	}
	
	@Override
	public Card removeFirst() {
		var first = CardStack.super.removeFirst();
		resizeBounds();
		return first;
	}
	
	@Override
	public Card removeLast() {
		var last = CardStack.super.removeLast();
		resizeBounds();
		return last;
	}
	
	@Override
	public Rectanglef getBounds() {
		return getCards().isEmpty() ? emptyBounds : cascadeBounds;
	}
	
	private void resizeBounds() {
		if (getCards().isEmpty()) {
			cascadeBounds.set(emptyBounds);
		} else {
			cascadeBounds.setMax(getLast().getBoundingBox().maxX, getLast().getBoundingBox().maxY);
		}
	}
	
	private float getNextVerticalOffset() {
		if (getCards().isEmpty()) {
			return 0;
		} else {
			if (getLast().isFaceUp()) {
				return emptyBounds.lengthY() / FACE_UP_OFFSET;
			} else {
				return emptyBounds.lengthY() / FACE_DOWN_OFFSET;
			}
		}
	}
	
//	@Override
//	public boolean containsPoint(Vector2f point) {
//		return (getCards().isEmpty() && getBounds().containsPoint(point))
//				|| (!isEmpty() && stream().anyMatch(card -> card.isFaceUp() && card.getBoundingBox().containsPoint(point)));
//	}
	
	@Override
	public void clicked(Vector2f clickPosition, List<Card> cardsHeld, CardStack previous) {
		if (cardsHeld.isEmpty()) {
			for (int i = getCards().size() - 1; i >= 0; i--) {
				if (get(i).getBoundingBox().containsPoint(clickPosition) && get(i).isFaceUp()) {
					var pileList = getCards().subList(i, getCards().size());
					pileList.forEach(card -> card.setClickOffset(clickPosition.x, clickPosition.y));
					cardsHeld.addAll(pileList);
//					getCards().removeAll(pileList);
					break;
				}
			}
		} else {
			if (isStackable(cardsHeld)) {
				if (previous instanceof Cascade previousCascade) {
					int previousCardIndex = previousCascade.getCards().indexOf(cardsHeld.getFirst()) - 1;
					scene.executeOperation(new MoveFromCascadeOperation(cardsHeld, previousCascade, this,
							previousCardIndex >= 0 && !previousCascade.get(previousCardIndex).isFaceUp()));
				} else {
					scene.executeOperation(new CardMoveOperation(cardsHeld, previous, this));
				}
			} else {
				previous.reset(cardsHeld);
			}
			cardsHeld.clear();
		}
	}
	
	public void grab(Vector2f clickPosition, List<Card> hand) {
		getCards().stream()
		.filter(card -> card.isFaceUp() && card.getBoundingBox().containsPoint(clickPosition))
		.findFirst().ifPresent(grabbedCard -> {
			var grabbedStack = getCards().subList(getCards().indexOf(grabbedCard), getCards().size());
			grabbedStack.forEach(card -> {
				card.setClickOffset(clickPosition.x, clickPosition.y);
				card.hold(this);
			});
			hand.addAll(grabbedStack);
			getCards().removeAll(grabbedStack);
		});
	}
	
	@Override
	public void reset(List<Card> cards) {
		addCards(cards);
		cards.clear();
		
		
//		addAll(cards);
//		for (int i = 0; i < size(); i++) {
//			get(i).setPosition(new Vector2f(
//					bounds.minX,
//					i == 0 ? bounds.minY : get(i - 1).getBoundingBox().minY + getCardVerticalOffset(i - 1)));
//		}
//		cards.clear();
	}
	
	@Override
	public void cardTaken() {
		if (getCards().isEmpty()) {
			return;
		}
		getLast().setFaceUp(true);
	}
	
	/**
	 * @param cards
	 * @return
	 */
	public boolean isStackable(List<Card> cards) {
		return shouldStartNewStack(cards) || shouldAddToStack(cards);
	}
	
	private boolean shouldStartNewStack(List<Card> cards) {
		return getCards().isEmpty() && cards.getFirst().getRank() == Rank.KING.ordinal();
	}
	
	private boolean shouldAddToStack(List<Card> cards) {
		return !getCards().isEmpty() 
				&& cards.getFirst().getRank() == getLast().getRank() - 1 
				&& Suit.isOpposite(cards.getFirst().getSuit(), getLast().getSuit());
	}

}
