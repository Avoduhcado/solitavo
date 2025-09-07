package com.avogine.solitavo.scene.cards;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

/**
 *
 */
public class Card {
	
	/**
	 * TODO Convert this into a simple ratio for easier scaling?
	 * The default width and height of a card.
	 */
	public static final Vector2f DEFAULT_SIZE = new Vector2f(72f, 100f);
	
	private static int cardBack = 4;

	private final Vector2f position;
	private final Vector2f size;
	private final Rank rank;
	private final Suit suit;
	private boolean faceUp;
	
	private boolean selected;
	
	private final Rectanglef boundingBox;
	
	/**
	 * @param position 
	 * @param size 
	 * @param rank 
	 * @param suit 
	 * 
	 */
	public Card(Vector2f position, Vector2f size, Rank rank, Suit suit) {
		this.position = position;
		this.size = size;
		this.rank = rank;
		this.suit = suit;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.y + size.y);
	}
	
	/**
	 * @param position
	 * @param rank
	 * @param suit
	 */
	public Card(Vector2f position, Rank rank, Suit suit) {
		this.position = position;
		this.size = Card.DEFAULT_SIZE;
		this.rank = rank;
		this.suit = suit;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.y + size.y);
	}
	
	private void updateBoundingBox() {
		boundingBox.setMin(position.x, position.y).setMax(position.x + size.x, position.y + size.y);
	}
	
	/**
	 * @return the position
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		position.set(x, y);
		updateBoundingBox();
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector2f position) {
		setPosition(position.x, position.y);
	}
	
	/**
	 * @return the size
	 */
	public Vector2f getSize() {
		return size;
	}

	/**
	 * @param width
	 * @param height
	 */
	public void setSize(float width, float height) {
		size.set(width, height);
		updateBoundingBox();
	}
	
	/**
	 * @param size the size to set
	 */
	public void setSize(Vector2f size) {
		setSize(size.x, size.y);
	}
	
	/**
	 * @return the boundingBox
	 */
	public Rectanglef getBoundingBox() {
		return boundingBox;
	}
	
	/**
	 * @return the rank
	 */
	public Rank getRank() {
		if (isFaceUp()) {
			return rank;
		}
		return Rank.values()[cardBack];
	}

	/**
	 * @return the suit
	 */
	public Suit getSuit() {
		if (isFaceUp()) {
			return suit;
		}
		return Suit.BONUS;
	}

	/**
	 * @return the faceUp
	 */
	public boolean isFaceUp() {
		return faceUp;
	}
	
	/**
	 * @param faceUp the faceUp to set
	 */
	public void setFaceUp(boolean faceUp) {
		this.faceUp = faceUp;
	}
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * @param cardBack
	 */
	public static void setCardBack(int cardBack) {
		if (cardBack < 0 || cardBack > 13) {
			return;
		}
		Card.cardBack = cardBack;
	}
	
	@Override
	public String toString() {
		return "Card " + rank + " of " + suit.displayName;
	}
}
