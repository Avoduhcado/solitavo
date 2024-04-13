package com.avogine.solitavo.scene.wild.util;

import java.util.List;

import com.avogine.solitavo.scene.wild.cards.Card;

/**
 *
 */
public interface CardSupplier {
	
	/**
	 * @param cards
	 * @return 
	 */
	public List<Card> removeCards(List<Card> cards);

}
