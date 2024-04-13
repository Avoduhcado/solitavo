package com.avogine.solitavo.scene.wild;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL14C.*;

import java.util.*;
import java.util.stream.Stream;

import org.joml.Vector2f;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.io.Window;
import com.avogine.io.event.MouseEvent;
import com.avogine.io.listener.*;
import com.avogine.render.data.TextureAtlas;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.util.CardStack;

/**
 *
 */
public class WildScene extends Scene implements MouseButtonListener, MouseMotionListener {
	
	private SpriteRenderer spriteRenderer;
	
	private TextureAtlas texture;
	
	private Stock stock;
	private Waste waste;
	private Foundation[] foundations;
	private Pile[] tableau;
	private Hand hand;
	
	private final Vector2f lastMouse = new Vector2f();
	
	@Override
	public void init(Game game, Window window) {
		projection.setOrtho2D(0, 504, 500, 0);
		spriteRenderer = new SpriteRenderer();
		
		game.register(spriteRenderer);
		
		game.addInputListener(this);
		
		texture = TextureCache.getInstance().getTextureAtlas("Cardsheet.png", Rank.values().length, Suit.values().length);
		
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < 52; i++) {
			cards.add(new Card(new Vector2f(), new Vector2f(72f, 100f), Rank.values()[i % 13], Suit.values()[i / 13]));
		}
		Collections.shuffle(cards);
		
		stock = new Stock(1);
		stock.addCards(cards);
		
		waste = new Waste();
		
		foundations = new Foundation[4];
		for (int i = 0; i < foundations.length; i++) {
			foundations[i] = new Foundation(i);
		}
		
		tableau = new Pile[7];
		for (int i = 0; i < tableau.length; i++) {
			tableau[i] = new Pile(i);
		}
		for (int x = 0; x < 7; x++) {
			for (int y = x; y < 7; y++) {
				tableau[y].dealCard(stock.getCards().removeLast());
			}
			tableau[x].revealTopCard();
		}
		
		hand = new Hand();
	}
	
	// Debug
//	private void splayDeck() {
//		for (int i = 0; i < cards.size(); i++) {
//			cards.get(i).setPosition((i % 13) * 18f, (float) Math.floor(i / 13.0) * 100);
//			cards.get(i).setFaceUp(true);
//		}
//	}

	@Override
	public void onRender(Window window) {
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		
		glClearColor(36f / 255f, 115f / 255f, 69f / 255f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		stock.draw(spriteRenderer, texture);
		waste.draw(spriteRenderer, texture);
		for (Foundation foundation : foundations) {
			foundation.draw(spriteRenderer, texture);
		}
		for (Pile pile : tableau) {
			pile.draw(spriteRenderer, texture);
		}
		
		hand.draw(spriteRenderer, texture);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		event.transformPoint(projection);
		
		if (waste.getBoundingBox().containsPoint(event.mouseX, event.mouseY)) {
			waste.getCard().ifPresent(card -> hand.autoPlaceCard(List.of(card), waste, CardStack.concatWithStream(foundations, tableau)));
		} else if (Stream.of(foundations).anyMatch(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.getTopCard().isPresent())) {
			Stream.of(foundations)
			.filter(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.getTopCard().isPresent())
			.findFirst()
			.ifPresent(foundation -> hand.autoPlaceCard(List.of(foundation.getTopCard().get()), foundation, List.of(tableau)));
		} else if (Stream.of(tableau).anyMatch(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && !pile.isEmpty())) {
			Stream.of(tableau)
			.filter(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && !pile.isEmpty())
			.findFirst()
			.ifPresent(pile -> hand.autoPlaceCard(pile.getCardsFromPoint(event.mouseX, event.mouseY), pile, CardStack.concatWithStream(foundations, tableau)));
		}
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		event.transformPoint(projection);
		lastMouse.set(event.mouseX, event.mouseY);
		
		if (stock.getBoundingBox().containsPoint(event.mouseX, event.mouseY)) {
			if (stock.getCards().isEmpty()) {
				stock.addCards(waste.recycleCards());
			} else {
				waste.addCards(stock.removeCards(List.of()));
			}
		} else if (waste.getBoundingBox().containsPoint(event.mouseX, event.mouseY)) {
			waste.getCard().ifPresent(card -> hand.holdCard(card, waste));
		} else if (Stream.of(foundations).anyMatch(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.getTopCard().isPresent())) {
			Stream.of(foundations)
			.filter(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.getTopCard().isPresent())
			.findFirst()
			.ifPresent(foundation -> hand.holdCard(foundation.getTopCard().get(), foundation));
		} else if (Stream.of(tableau).anyMatch(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && !pile.isEmpty())) {
			Stream.of(tableau)
			.filter(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && !pile.isEmpty())
			.findFirst()
			.ifPresent(pile -> hand.holdCards(pile.getCardsFromPoint(event.mouseX, event.mouseY), pile));
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		event.transformPoint(projection);
		
		if (!hand.isHolding()) {
			return;
		}
		Stream.of(foundations)
		.filter(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.canStack(hand.getCards()))
		.findFirst()
		.ifPresent(foundation -> hand.placeCards(foundation));
		
		if (!hand.isHolding()) {
			return;
		}
		Stream.of(tableau)
		.filter(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && pile.canStack(hand.getCards()))
		.findFirst()
		.ifPresent(pile -> hand.placeCards(pile));

		hand.removeCards();
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		// Not Implemented
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		event.transformPoint(projection);
		if (hand.isHolding()) {
			float x = event.mouseX;
			float y = event.mouseY;
			hand.moveCards(x - lastMouse.x, y - lastMouse.y);
			
			lastMouse.set(x, y);
		}
	}

}
