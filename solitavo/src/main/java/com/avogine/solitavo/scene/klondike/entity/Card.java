package com.avogine.solitavo.scene.klondike.entity;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.scene.klondike.CardStack;

/**
 *
 */
public class Card {

	private Vector2f position;
	private Vector2f size;
	private float rotation;
	private float scale;
	private int rank;
	private int suit;
	private boolean faceUp;
	
	private final Rectanglef boundingBox;
	private final Vector2f clickOffset;
	private CardStack previousStack;
	
	/**
	 * @param position 
	 * @param size 
	 * @param rotation 
	 * @param scale
	 * @param rank 
	 * @param suit 
	 * 
	 */
	public Card(Vector2f position, Vector2f size, float rotation, float scale, int rank, int suit) {
		setPosition(position);
		setSize(size);
		setRotation(rotation);
		setScale(scale);
		setRank(rank);
		setSuit(suit);
		
		boundingBox = new Rectanglef(position, position.add(size, new Vector2f()));
		clickOffset = new Vector2f();
	}
	
	/**
	 * 
	 * @return the dimension that encompasses this card without scaling applied.
	 */
	public Rectanglef getBoundingBox() {
		boundingBox.setMin(position);
		boundingBox.setMax(position.x + size.x, position.y + size.y);
		return boundingBox;
	}
	
	/**
	 * @return
	 */
	public Vector2f getClickOffset() {
		return clickOffset;
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void setClickOffset(float x, float y) {
		clickOffset.x = position.x - x;
		clickOffset.y = position.y - y;
	}
	
	/**
	 * 
	 */
	public void hold(CardStack previousStack) {
		this.previousStack = previousStack;
	}
	
	public void reset() {
		if (previousStack == null) {
			return;
		}
		
		previousStack.addCard(this);
		previousStack = null;
	}

	/**
	 * @return the position
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * TODO Add/Replace a two parameter method to avoid needing to allocate new vectors each time
	 * @param position the position to set
	 */
	public void setPosition(Vector2f position) {
		this.position = position;
	}

	/**
	 * @return the size
	 */
	public Vector2f getSize() {
		return size;
	}
	
	/**
	 * @return the half size
	 */
	public Vector2f getHalfSize() {
		return size.mul(0.5f, new Vector2f());
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Vector2f size) {
		this.size = size;
	}

	/**
	 * @return the rotation
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the suit
	 */
	public int getSuit() {
		return suit;
	}

	/**
	 * @param suit the suit to set
	 */
	public void setSuit(int suit) {
		this.suit = suit;
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
	
	@Override
	public String toString() {
		return "Card " + Rank.values()[rank] + " of " + Suit.values()[suit].displayName;
	}
	
}
