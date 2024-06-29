package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.*;
import org.joml.Math;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.util.CardHolder;

/**
 * 
 */
public class Stock implements CardHolder {

	private final List<Card> cards;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	private final Vector4f blankCardOffset;
	
	private DrawMode drawMode;
	
	/**
	 * @param drawMode 
	 */
	public Stock(DrawMode drawMode) {
		cards = new ArrayList<>();
		position = new Vector2f(0f, 0f);
		size = new Vector2f(72f, 100f);
		boundingBox = new Rectanglef(position, size);
		blankCardOffset = new Vector4f(
				(float) Rank.THREE.ordinal() / Rank.values().length,
				(float) Suit.BONUS.ordinal() / Suit.values().length,
				1f / Rank.values().length, 1f / Suit.values().length);
		
		this.drawMode = drawMode;
	}
	
	/**
	 * 
	 */
	public Stock() {
		this(DrawMode.STANDARD);
	}
	
	@Override
	public void addCards(List<Card> cards) {
		this.cards.addAll(cards);
		cards.forEach(card -> {
			card.setFaceUp(false);
			card.setPosition(position);
		});
	}

	@Override
	public List<Card> removeCards(List<Card> cards) {
		this.cards.removeAll(cards);
		return cards;
	}

	/**
	 * 
	 * @return
	 */
	public List<Card> getCardsToDraw() {
		return cards.reversed().subList(0, Math.min(drawMode.drawCount, cards.size()));
	}
	
	/**
	 * @param renderer
	 * @param texture
	 */
	public void render(SpriteRenderer renderer, TextureAtlas texture) {
		if (cards.isEmpty()) {
			renderer.renderSprite(position, size, texture.getId(), blankCardOffset);
		} else {
			var topCard = cards.getLast();
			renderer.renderSprite(topCard.getPosition(), topCard.getSize(), texture.getId(), topCard.computeTextureOffset(texture));
		}
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	/**
	 * @return the cards
	 */
	public List<Card> getCards() {
		return cards;
	}
	
	@Override
	public Rectanglef getBoundingBox() {
		return boundingBox;
	}
	
	/**
	 *
	 */
	public enum DrawMode {
		/**
		 * Deal three cards at a time from the Stock.
		 */
		STANDARD(3),
		/**
		 * Deal one card at a time from the Stock.
		 */
		SINGLE(1);
		
		int drawCount;
		
		DrawMode(int drawCount) {
			this.drawCount = drawCount;
		}
	}

}
