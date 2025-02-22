package com.avogine.solitavo.scene;

import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

import com.avogine.game.scene.*;
import com.avogine.render.*;
import com.avogine.solitavo.scene.cards.Card;
import com.avogine.solitavo.scene.klondike.*;
import com.avogine.solitavo.scene.klondike.Stock.DrawMode;

/**
 *
 */
public class KlondikeScene extends Scene {
	
	private final Stock stock;
	private final Waste waste;
	private final Foundation[] foundations;
	private final Pile[] tableau;
	private final Hand hand;

	private final Rectanglef foundationsBounds;
	private final Rectanglef tableauBounds;
	
	private final AtomicInteger moveCounter;
	
	private final TextureCache textureCache;
	private final FontCache fontCache;
	
	private DrawMode stockDraw = DrawMode.STANDARD;
	
	/**
	 * 
	 */
	public KlondikeScene() {
		super(new OrthoProjection(504, 500), new Camera());
		final Vector2f cardSize = Card.DEFAULT_SIZE;
		final Vector2f tableOffset = new Vector2f(0f, 24f);
		
		stock = new Stock(new Vector2f(0f, 0f).add(tableOffset), cardSize, stockDraw);
		waste = new Waste(new Vector2f(72f, 0f).add(tableOffset), cardSize);
		foundations = new Foundation[4];
		for (int i = 0; i < foundations.length; i++) {
			var foundationPosition = new Vector2f((cardSize.x * 3) + i * cardSize.x, 0f).add(tableOffset);
			foundations[i] = new Foundation(foundationPosition, cardSize);
		}
		tableau = new Pile[7];
		for (int i = 0; i < tableau.length; i++) {
			var pilePosition = new Vector2f(i * cardSize.x, cardSize.y).add(tableOffset);
			tableau[i] = new Pile(pilePosition, cardSize);
		}
		hand = new Hand();
		
		foundationsBounds = new Rectanglef();
		tableauBounds = new Rectanglef();
		
		moveCounter = new AtomicInteger();
		
		textureCache = new TextureCache();
		fontCache = new FontCache();
	}
	
	// Debug
//	private void splayDeck() {
//		for (int i = 0; i < cards.size(); i++) {
//			cards.get(i).setPosition((i % 13) * 18f, (float) Math.floor(i / 13.0) * 100);
//			cards.get(i).setFaceUp(true);
//		}
//	}
	
	/**
	 * Free allocated resources.
	 */
	public void cleanup() {
		textureCache.cleanup();
		fontCache.cleanup();
	}
	
	/**
	 * @return the stock
	 */
	public Stock getStock() {
		return stock;
	}
	
	/**
	 * @return the waste
	 */
	public Waste getWaste() {
		return waste;
	}
	
	/**
	 * @return the foundations
	 */
	public Foundation[] getFoundations() {
		return foundations;
	}
	
	/**
	 * @return the tableau
	 */
	public Pile[] getTableau() {
		return tableau;
	}
	
	/**
	 * @return the hand
	 */
	public Hand getHand() {
		return hand;
	}
	
	/**
	 * @return the foundationsBounds
	 */
	public Rectanglef getFoundationsBounds() {
		return foundationsBounds;
	}
	
	/**
	 * @return the tableauBounds
	 */
	public Rectanglef getTableauBounds() {
		return tableauBounds;
	}
	
	/**
	 * @return the moveCounter
	 */
	public AtomicInteger getMoveCounter() {
		return moveCounter;
	}
	
	/**
	 * @return the textureCache
	 */
	public TextureCache getTextureCache() {
		return textureCache;
	}
	
	/**
	 * @return the fontCache
	 */
	public FontCache getFontCache() {
		return fontCache;
	}
	
}
