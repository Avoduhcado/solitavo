package com.avogine.solitavo.scene.klondike;

import java.util.*;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.solitavo.render.SpriteRender;
import com.avogine.solitavo.render.data.TextureAtlas;
import com.avogine.solitavo.scene.cards.*;
import com.avogine.solitavo.scene.util.CardHolder;

/**
 * 
 */
public class Stock implements CardHolder {

	private final List<Card> cards;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	private DrawMode drawMode;
	
	/**
	 * @param position 
	 * @param size 
	 * @param drawMode 
	 */
	public Stock(Vector2f position, Vector2f size, DrawMode drawMode) {
		cards = new ArrayList<>();
		this.position = position;
		this.size = size;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.y + size.y);
		
		this.drawMode = drawMode;
	}
	
	/**
	 * @param position 
	 * @param size 
	 * 
	 */
	public Stock(Vector2f position, Vector2f size) {
		this(position, size, DrawMode.STANDARD);
	}
	
	/**
	 * 
	 */
	public void init() {
		cards.clear();
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
	public void render(SpriteRender renderer, TextureAtlas texture) {
		if (cards.isEmpty()) {
			// TODO The empty card space should probably be sourced from somewhere rather than just hardcoded here.
			renderer.renderSpriteAtlas(position, size, texture, Rank.THREE.ordinal(), Suit.BONUS.ordinal());
		} else {
			var topCard = cards.getLast();
			renderer.renderSpriteAtlas(topCard.getPosition(), topCard.getSize(), texture, topCard.getSuit().ordinal(), topCard.getRank().ordinal());
		}
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	/**
	 * @return
	 */
	public DrawMode getDrawMode() {
		return drawMode;
	}
	
	/**
	 * @param drawMode the drawMode to set
	 */
	public void setDrawMode(DrawMode drawMode) {
		this.drawMode = drawMode;
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
