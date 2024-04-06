package com.avogine.solitavo.scene.klondike.entity;

/**
 *
 */
public enum Suit {

	HEART("Hearts"),
	DIAMOND("Diamonds"),
	CLUB("Clubs"),
	SPADE("Spades"),
	BONUS("Bonus");
	
	final String displayName;
	
	Suit(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @param suit1
	 * @param suit2
	 * @return
	 */
	public static boolean isOpposite(int suit1, int suit2) {
		if (suit1 == HEART.ordinal() || suit1 == DIAMOND.ordinal()) {
			return suit2 == CLUB.ordinal() || suit2 == SPADE.ordinal();
		} else {
			return suit2 == HEART.ordinal() || suit2 == DIAMOND.ordinal();
		}
	}
}
