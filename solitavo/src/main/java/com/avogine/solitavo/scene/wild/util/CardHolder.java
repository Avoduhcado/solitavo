package com.avogine.solitavo.scene.wild.util;

import java.util.List;

import com.avogine.solitavo.scene.wild.cards.Card;

/**
 *
 */
public interface CardHolder {
	
	/**
	 * @param cards
	 */
	public void addCards(List<Card> cards);

	/**
	 * @param cards
	 * @return 
	 */
	public List<Card> removeCards(List<Card> cards);

}
