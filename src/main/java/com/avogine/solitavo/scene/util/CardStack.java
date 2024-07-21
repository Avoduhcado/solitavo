package com.avogine.solitavo.scene.util;

import java.util.*;
import java.util.stream.Stream;

import com.avogine.solitavo.scene.cards.Card;

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
	public static Stream<CardStack> concatWithStream(CardStack[] array1, CardStack[] array2) {
		return Stream.concat(Arrays.stream(array1), Arrays.stream(array2));
	}

	/**
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static List<CardStack> concatToList(CardStack[] array1, CardStack[] array2) {
		return concatWithStream(array1, array2).toList();
	}
	
	public static CardStack[] concatArrays(CardStack[] array1, CardStack[] array2) {
		var concattedArray = new CardStack[array1.length + array2.length];
		System.arraycopy(array1, 0, concattedArray, 0, array1.length);
		System.arraycopy(array2, 0, concattedArray, array1.length, array2.length);
		return concattedArray;
	}
	
}