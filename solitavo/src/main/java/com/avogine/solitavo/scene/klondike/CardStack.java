package com.avogine.solitavo.scene.klondike;

import java.util.List;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.klondike.entity.Card;

/**
 *
 */
public sealed interface CardStack permits Cascade, Foundation, Waste, Stock {

	/**
	 * @return the List of {@link Card}s contained in this stack.
	 */
	public List<Card> getCards();
	
	/**
	 * @return the first {@link Card} in the stack or null.
	 */
	public default Card getFirst() {
		if (getCards().isEmpty()) {
			return null;
		}
		return getCards().getFirst();
	}
	
	/**
	 * @return and removes the first {@link Card} in the stack or null;
	 */
	public default Card removeFirst() {
		if (getCards().isEmpty()) {
			return null;
		}
		return getCards().removeFirst();
	}
	
	/**
	 * @return the last {@link Card} in the stack or null.
	 */
	public default Card getLast() {
		if (getCards().isEmpty()) {
			return null;
		}
		return getCards().getLast();
	}
	
	/**
	 * @return and removes the last {@link Card} in the stack or null.
	 */
	public default Card removeLast() {
		if (getCards().isEmpty()) {
			return null;
		}
		return getCards().removeLast();
	}
	
	/**
	 * @param index The index in the stack to retrieve a {@link Card} at.
	 * @return The {@link Card} at the specified index.
	 * @see List#get(int)
	 */
	public default Card get(int index) {
		return getCards().get(index);
	}
	
	/**
	 * @param index The index in the stack to remove a {@link Card} at.
	 * @return The {@link Card} previously at the specified index.
	 * @see List#remove(int)
	 */
	public default Card remove(int index) {
		return getCards().remove(index);
	}
	
	/**
	 * @param card
	 * @return
	 */
	public default boolean removeCard(Card card) {
		return getCards().remove(card);
	}
	
	/**
	 * @param cardList
	 * @return
	 */
	public default void removeCards(List<Card> cardList) {
		getCards().removeAll(cardList);
	}
	
	/**
	 * Append a {@link Card} to this stack.
	 * @param card The {@link Card} to add.
	 */
	public void addCard(Card card);
	
	/**
	 * Append a List of {@link Card}s to this stack.
	 * @param cardList The List of {@link Card}s to add.
	 */
	public void addCards(List<Card> cardList);
	
	/**
	 * @return The rectangular bounding box of this stack.
	 */
	public Rectanglef getBounds();
	
	/**
	 * @param point a two dimensional coordinate in the scene.
	 * @return true if this stack's bounds contains the given Vector2f.
	 */
	public default boolean containsPoint(Vector2f point) {
		return getBounds().containsPoint(point);
	}
	
	/**
	 * @param clickPosition
	 * @param cardsHeld
	 * @param previous 
	 */
	public void clicked(Vector2f clickPosition, List<Card> cardsHeld, CardStack previous);
	
	/**
	 * @param cards
	 */
	public void reset(List<Card> cards);
	
	/**
	 * 
	 */
	public default void cardTaken() {
		
	}
	
}
