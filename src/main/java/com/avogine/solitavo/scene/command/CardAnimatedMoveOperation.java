package com.avogine.solitavo.scene.command;

import java.util.List;

import org.joml.Vector2f;

import com.avogine.solitavo.scene.cards.Card;
import com.avogine.solitavo.scene.klondike.Pile;
import com.avogine.solitavo.scene.util.CardHolder;
import com.avogine.util.EasingUtils;

/**
 *
 */
public class CardAnimatedMoveOperation extends CardMoveOperation {

	private List<Vector2f> cardStarts;
	
	private float duration;
	private float time;
	
	/**
	 * @param cards
	 * @param origin
	 * @param destination
	 * @param reverseOrdering 
	 */
	public CardAnimatedMoveOperation(List<Card> cards, CardHolder origin, CardHolder destination, boolean reverseOrdering) {
		super(cards, origin, destination, reverseOrdering);
		cardStarts = cards.stream()
				.map(card -> new Vector2f().set(card.getPosition()))
				.toList();
		duration = 0.2f;
		time = 0f;
	}
	
	/**
	 * @param cards 
	 * @param origin 
	 * @param destination 
	 */
	public CardAnimatedMoveOperation(List<Card> cards, CardHolder origin, CardHolder destination) {
		this(cards, origin, destination, false);
	}
	
	@Override
	public void execute(float delta) {
		time = Math.clamp(time + delta, 0, duration);
		cards.forEach(card -> {
			var cardStartingPosition = cardStarts.get(cards.indexOf(card));
			float x = EasingUtils.easeOutQuad(time, cardStartingPosition.x, destination.getNextSpace().x - cardStartingPosition.x, duration);
			float y = EasingUtils.easeOutQuad(time, cardStartingPosition.y, destination.getNextSpace().y - cardStartingPosition.y, duration);
			card.setPosition(x, y);
			card.setSelected(isExecuting());
		});
	}
	
	@Override
	public void commit() {
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
	
	/**
	 * @return
	 */
	@Override
	public boolean isExecuting() {
		return time < duration;
	}
}
