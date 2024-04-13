package com.avogine.solitavo.scene.wild.util;

import java.util.*;
import java.util.stream.Stream;

import com.avogine.solitavo.scene.wild.cards.Card;

/**
 *
 */
public interface CardStack extends CardHolder {

	/**
	 * @param cards
	 * @return
	 */
	public boolean canStack(List<Card> cards);

	/**
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static List<CardStack> concatWithStream(CardStack[] array1, CardStack[] array2) {
		return Stream.concat(Arrays.stream(array1), Arrays.stream(array2)).toList();
	}

}