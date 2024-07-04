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
	
	private final Rectanglef boundingBox;
	
	/**
	 * @param position 
	 * @param size 
	 * 
	 */
	public Waste(Vector2f position, Vector2f size) {
		cards = new ArrayList<>();
		this.position = position;
		boundingBox = new Rectanglef(position.x, position.y, position.x + size.x, position.x + size.y);
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
	public void render(SpriteRenderer renderer, TextureAtlas texture) {
		cards.stream()
		.dropWhile(card -> cards.indexOf(card) < cards.size() - 3)
		.forEach(card -> renderer.renderSprite(card.getPosition(), card.getSize(), texture.getId(), card.computeTextureOffset(texture)));
	}
	
	private void splayCards() {
		cards.forEach(card -> card.setPosition(position));
		var topCards = cards.subList(Math.max(0, cards.size() - 3), cards.size());
		for (int i = 0; i < topCards.size(); i++) {
			topCards.get(i).setFaceUp(true);
			topCards.get(i).setPosition(position.x + (i * 18), position.y);
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
	 * Returns specifically the bounds of the top card revealed in the waste when the waste is not empty.
	 */
	@Override
	public Rectanglef getBoundingBox() {
		if (cards.isEmpty()) {
			return boundingBox;
		} else {
			return cards.getLast().getBoundingBox();
		}
	}

}
