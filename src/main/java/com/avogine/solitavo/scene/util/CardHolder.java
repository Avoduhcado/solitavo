package com.avogine.solitavo.scene.util;

import java.util.List;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.cards.Card;

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
	
	/**
	 * @return
	 */
	public Vector2f getNextSpace();

	/**
	 * @return the rectangular bounds of this holder.
	 */
	public Rectanglef getBoundingBox();

}
