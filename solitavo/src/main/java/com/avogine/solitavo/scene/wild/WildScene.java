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
import org.lwjgl.glfw.GLFW;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.game.ui.nuklear.DebugInfo;
import com.avogine.io.Window;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.render.data.TextureAtlas;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.Stock.DrawMode;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.command.*;
import com.avogine.solitavo.scene.wild.util.CardStack;

/**
 *
 */
public class WildScene extends Scene implements MouseButtonListener, MouseMotionListener, KeyListener {
	
	private SpriteRenderer spriteRenderer;
	
	private TextureAtlas texture;
	
	private Stock stock;
	private Waste waste;
	private Foundation[] foundations;
	private Pile[] tableau;
	private Hand hand;
	
	private final Vector2f lastMouse = new Vector2f();
	
	private DrawMode stockDraw = DrawMode.STANDARD;
	
	private final ArrayDeque<CardOperation> operations = new ArrayDeque<>();
	
	private final Random random = new Random();
	private long seed;
	
	@Override
	public void init(Game game, Window window) {
		projection.setOrtho2D(0, 504, 500, 0);
		spriteRenderer = new SpriteRenderer();
		
		game.register(spriteRenderer);
		game.register(new DebugInfo());
		
		game.addInputListener(this);
		
		texture = TextureCache.getInstance().getTextureAtlas("Cardsheet.png", Rank.values().length, Suit.values().length);
		
		setupTable(8675309343L ^ System.currentTimeMillis());
	}
	
	private void setupTable(long seed) {
		this.seed = seed;
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < 52; i++) {
			cards.add(new Card(new Vector2f(), new Vector2f(72f, 100f), Rank.values()[i % 13], Suit.values()[i / 13]));
		}
		random.setSeed(seed);
		Collections.shuffle(cards, random);
		
		stock = new Stock(stockDraw);
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
		
		operations.clear();
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
	
	private void executeOperation(CardOperation operation) {
		operations.add(operation);
		operation.execute();
	}
	
	private void undoOperation() {
		if (operations.peekLast() != null) {
//			operations.peekLast().describe();
			operations.pollLast().rollback();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		event.transformPoint(projection);
		
		if (waste.getBoundingBox().containsPoint(event.mouseX, event.mouseY)) {
			waste.getCard().ifPresent(card -> hand.autoPlaceCard(List.of(card), List.of(CardStack.concatArrays(foundations, tableau)))
					.ifPresent(cardStack -> executeOperation(new CardMoveOperation(List.of(card), waste, cardStack))));
		} else if (Stream.of(foundations).anyMatch(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.getTopCard().isPresent())) {
			Stream.of(foundations)
			.filter(foundation -> foundation.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && foundation.getTopCard().isPresent())
			.findFirst()
			.ifPresent(foundation -> hand.autoPlaceCard(List.of(foundation.getTopCard().get()), List.of(tableau))
					.ifPresent(cardStack -> executeOperation(new CardMoveOperation(List.of(foundation.getTopCard().get()), foundation, cardStack))));
		} else if (Stream.of(tableau).anyMatch(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && !pile.isEmpty())) {
			Stream.of(tableau)
			.filter(pile -> pile.getBoundingBox().containsPoint(event.mouseX, event.mouseY) && !pile.isEmpty())
			.findFirst()
			.ifPresent(pile -> hand.autoPlaceCard(pile.getCardsFromPoint(event.mouseX, event.mouseY), List.of(CardStack.concatArrays(foundations, tableau)))
					.ifPresent(cardStack -> executeOperation(new CardMoveOperation(pile.getCardsFromPoint(event.mouseX, event.mouseY), pile, cardStack))));
		}
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		// TODO For some reason mouse click events and press/release events conflict when activating a pile that reveals a card that can be auto placed and fires two moves in one sequence of mouse actions
		event.transformPoint(projection);
		lastMouse.set(event.mouseX, event.mouseY);
		
		if (stock.getBoundingBox().containsPoint(event.mouseX, event.mouseY)) {
			if (stock.getCardsToDraw().isEmpty()) {
				executeOperation(new CardMoveOperation(waste.getRecycleCards(), waste, stock, true));
			} else {
				executeOperation(new CardMoveOperation(stock.getCardsToDraw(), stock, waste, true));
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
		.filter(foundation -> foundation.getBoundingBox().intersectsRectangle(hand.getBoundingBox()) && foundation.canStack(hand.getCards()))
		.findFirst()
		.ifPresent(foundation -> executeOperation(hand.placeCards(foundation)));
		
		if (!hand.isHolding()) {
			return;
		}
		Stream.of(tableau)
		.filter(pile -> pile.getBoundingBox().intersectsRectangle(hand.getBoundingBox()) && pile.canStack(hand.getCards()))
		.findFirst()
		.ifPresent(pile -> executeOperation(hand.placeCards(pile)));

		if (hand.isHolding()) {
			hand.removeCards();
		}
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

	@Override
	public void keyPressed(KeyEvent event) {
		// Not Implemented
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (hand.isHolding()) {
			// Don't perform undo operations while holding a card, likely unnecessary for setupTable but good to avoid for now.
			return;
		}
		switch (event.key) {
			case GLFW.GLFW_KEY_F -> undoOperation();
			case GLFW.GLFW_KEY_X -> setupTable(seed);
			case GLFW.GLFW_KEY_A -> setupTable(8675309343L ^ System.currentTimeMillis());
			case GLFW.GLFW_KEY_0 -> Card.setCardBack(0);
			case GLFW.GLFW_KEY_3 -> Card.setCardBack(3);
			case GLFW.GLFW_KEY_4 -> Card.setCardBack(4);
			case GLFW.GLFW_KEY_5 -> Card.setCardBack(5);
			case GLFW.GLFW_KEY_6 -> Card.setCardBack(6);
			case GLFW.GLFW_KEY_7 -> Card.setCardBack(7);
			case GLFW.GLFW_KEY_1 -> stockDraw = (stockDraw == DrawMode.STANDARD ? DrawMode.SINGLE : DrawMode.STANDARD);
			default -> {
				// Unbound key
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// Not Implemented
	}

}
