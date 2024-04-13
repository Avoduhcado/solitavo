package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.*;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.*;

/**
 * 
 */
public class Stock {

	private final List<Card> cards;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	private final Vector4f blankCardOffset;
	
	private int drawCount;
	
	/**
	 * 
	 */
	public Stock(int drawCount) {
		cards = new ArrayList<>();
		position = new Vector2f(0f, 0f);
		size = new Vector2f(72f, 100f);
		boundingBox = new Rectanglef(position, size);
		blankCardOffset = new Vector4f(
				(float) Rank.THREE.ordinal() / Rank.values().length,
				(float) Suit.BONUS.ordinal() / Suit.values().length,
				1f / Rank.values().length, 1f / Suit.values().length);
		
		this.drawCount = drawCount;
	}
	
	/**
	 * 
	 */
	public Stock() {
		this(3);
	}
	
	/**
	 * @param cards
	 */
	public void addCards(List<Card> cards) {
		this.cards.addAll(cards);
		cards.forEach(card -> {
			card.setFaceUp(false);
			card.setPosition(position);
		});
	}
	
	/**
	 * @return
	 */
	public List<Card> takeCards() {
		var drawnCards = new ArrayList<Card>();
		int i = drawCount;
		while (i > 0 && !cards.isEmpty()) {
			drawnCards.add(cards.removeLast());
			i--;
		}
		return drawnCards;
	}
	
	/**
	 * @param renderer
	 * @param texture
	 */
	public void draw(SpriteRenderer renderer, TextureAtlas texture) {
		if (cards.isEmpty()) {
			renderer.drawSprite(position, size, texture.getId(), blankCardOffset);
		} else {
			var topCard = cards.getLast();
			renderer.drawSprite(topCard.getPosition(), topCard.getSize(), texture.getId(), topCard.computeTextureOffset(texture));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Card> getCards() {
		return cards;
	}
	
	/**
	 * 
	 * @return
	 */
	public Rectanglef getBoundingBox() {
		return boundingBox;
	}
	
}
