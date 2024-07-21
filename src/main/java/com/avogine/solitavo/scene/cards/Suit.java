package com.avogine.solitavo.scene.cards;

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
	public static boolean isOpposite(Suit suit1, Suit suit2) {
		if (suit1 == HEART || suit1 == DIAMOND) {
			return suit2 == CLUB || suit2 == SPADE;
		} else {
			return suit2 == HEART || suit2 == DIAMOND;
		}
	}
}
