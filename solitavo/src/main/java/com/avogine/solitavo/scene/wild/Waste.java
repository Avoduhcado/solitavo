package com.avogine.solitavo.scene.wild;

import java.util.*;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.render.data.TextureAtlas;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.Card;
import com.avogine.solitavo.scene.wild.util.CardHolder;

/**
 *
 */
public class Waste implements CardHolder {
	
	private final List<Card> cards;
	
	private final Vector2f position;
	
	private final Vector2f size;
	
	private final Rectanglef boundingBox;
	
	/**
	 * 
	 */
	public Waste() {
		cards = new ArrayList<>();
		position = new Vector2f(72f, 0f);
		size = new Vector2f(72f, 100f);
		boundingBox = new Rectanglef(position, position.add(size, new Vector2f()));
	}
	
	@Override
	public void addCards(List<Card> cards) {
		this.cards.addAll(cards);
		splayCards();
	}

	/**
	 * @return
	 */
	public List<Card> getRecycleCards() {
		return cards.reversed();
	}

	/**
	 * @return
	 */
	public List<Card> recycleCards() {
		var recycledCards = new ArrayList<Card>();
		recycledCards.addAll(cards.reversed());
		cards.clear();
		return recycledCards;
	}
	
	@Override
	public List<Card> removeCards(List<Card> cards) {
		this.cards.removeAll(cards);
		splayCards();
		return cards;
	}
	
	/**
	 * @param renderer
	 * @param texture
	 */
	public void draw(SpriteRenderer renderer, TextureAtlas texture) {
		cards.stream()
		.dropWhile(card -> cards.indexOf(card) < cards.size() - 3)
		.forEach(card -> renderer.drawSprite(card.getPosition(), card.getSize(), texture.getId(), card.computeTextureOffset(texture)));
	}
	
	private void splayCards() {
		cards.forEach(card -> card.setPosition(position));
		var topCards = cards.subList(Math.max(0, cards.size() - 3), cards.size());
		for (int i = 0; i < topCards.size(); i++) {
			topCards.get(i).setFaceUp(true);
			topCards.get(i).setPosition(position.x + (i * 18), 0);
		}
	}
	
	/**
	 * @return
	 */
	public Optional<Card> getCard() {
		if (cards.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(cards.getLast());
	}
	
	/**
	 * @return
	 */
	public Rectanglef getBoundingBox() {
		if (cards.isEmpty()) {
			return boundingBox;
		} else {
			return cards.getLast().getBoundingBox();
		}
	}

}
