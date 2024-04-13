package com.avogine.solitavo.scene.wild.cards;

import org.joml.*;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;

/**
 *
 */
public class Card {
	
	private static final Vector4f TEXTURE_OFFSET = new Vector4f();
	
	private static int CARD_BACK = 4;

	private final Vector2f position;
	private final Vector2f size;
	private final Rank rank;
	private final Suit suit;
	private boolean faceUp;
	
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
		boundingBox = new Rectanglef(position, position.add(size, new Vector2f()));
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
	
	private void updateBoundingBox() {
		boundingBox.setMin(position).setMax(position.x + size.x, position.y + size.y);
	}

	/**
	 * @return the rank
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * @return the suit
	 */
	public Suit getSuit() {
		return suit;
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
		return "Card " + rank + " of " + suit.displayName;
	}
	
	/**
	 * @param atlas
	 * @return
	 */
	public Vector4f computeTextureOffset(TextureAtlas atlas) {
		TEXTURE_OFFSET.set((float) (isFaceUp() ? getRank().ordinal() : CARD_BACK) / Rank.values().length,
				(float) (isFaceUp() ? getSuit().ordinal() : Suit.BONUS.ordinal()) / Suit.values().length,
				1f / atlas.getColumns(), 1f / atlas.getRows());
		return TEXTURE_OFFSET;
	}
	
}
