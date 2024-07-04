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
import org.joml.primitives.Rectanglef;
import org.lwjgl.glfw.GLFW;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.io.Window;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.render.TextRenderer;
import com.avogine.render.data.*;
import com.avogine.render.loader.font.FontCache;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.solitavo.scene.render.SpriteRenderer;
import com.avogine.solitavo.scene.wild.Stock.DrawMode;
import com.avogine.solitavo.scene.wild.cards.*;
import com.avogine.solitavo.scene.wild.command.*;
import com.avogine.solitavo.scene.wild.util.*;
import com.avogine.util.Pair;

/**
 *
 */
public class WildScene extends Scene implements MouseButtonListener, MouseMotionListener, KeyListener {
	
	private SpriteRenderer spriteRenderer;
	
	private TextRenderer textRenderer;
	
	private TextureAtlas texture;
	
	private Stock stock;
	private Waste waste;
	private Foundation[] foundations;
	private Pile[] tableau;
	private Hand hand;
	
	private final Rectanglef foundationsBounds = new Rectanglef();
	private final Rectanglef tableauBounds = new Rectanglef();
	
	private final Vector2f lastMouse = new Vector2f();
	
	private DrawMode stockDraw = DrawMode.STANDARD;
	
	private final ArrayDeque<CardOperation> operations = new ArrayDeque<>();
	
	private FontDetails uiFont;
	private int moveCounter;
	
	private final Random random = new Random();
	private long seed;
	
	@Override
	public void init(Game game, Window window) {
		projection.setOrtho2D(0, 504, 500, 0);
		spriteRenderer = new SpriteRenderer();
		textRenderer = new TextRenderer();
		
		game.register(spriteRenderer);
		game.register(textRenderer);
		
		game.addInputListener(this);
		
		texture = TextureCache.getInstance().getTextureAtlas("Cardsheet.png", Rank.values().length, Suit.values().length);
		
		uiFont = FontCache.getInstance().getFont("alagard.ttf");
		
		setupTable(random.nextLong());
	}
	
	private void setupTable(long seed) {
		this.seed = seed;
		random.setSeed(seed);
		
		setupTable();
	}
	
	private void setupTable() {
		moveCounter = 0;
		final Vector2f cardSize = Card.DEFAULT_SIZE;
		final Vector2f tableOffset = new Vector2f(0f, 24f);
		
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < 52; i++) {
			int rankIndex = i % Rank.values().length;
			int suitIndex = i / Rank.values().length;
			cards.add(new Card(new Vector2f(), cardSize, Rank.values()[rankIndex], Suit.values()[suitIndex]));
		}
		random.setSeed(seed);
		Collections.shuffle(cards, random);
		
		stock = new Stock(new Vector2f(0f, 0f).add(tableOffset), cardSize, stockDraw);
		stock.addCards(cards);
		
		waste = new Waste(new Vector2f(72f, 0f).add(tableOffset), cardSize);
		
		foundations = new Foundation[4];
		for (int i = 0; i < foundations.length; i++) {
			var foundationPosition = new Vector2f((cardSize.x * 3) + i * cardSize.x, 0f).add(tableOffset);
			foundations[i] = new Foundation(foundationPosition, cardSize);
		}
		foundationsBounds.set(foundations[0].getBoundingBox());
		computeFoundationBounds();
		
		tableau = new Pile[7];
		for (int i = 0; i < tableau.length; i++) {
			var pilePosition = new Vector2f(i * cardSize.x, cardSize.y).add(tableOffset);
			tableau[i] = new Pile(pilePosition, cardSize);
		}
		for (int x = 0; x < 7; x++) {
			for (int y = x; y < 7; y++) {
				tableau[y].dealCard(stock.getCards().removeLast());
			}
			tableau[x].revealTopCard();
		}
		tableauBounds.set(tableau[0].getBoundingBox());
		computeTableauBounds();
		
		hand = new Hand();
		
		operations.clear();
	}

	private Rectanglef computeFoundationBounds() {
		foundationsBounds.setMin(foundationsBounds.minX, foundationsBounds.minY).setMax(foundationsBounds.minX, foundationsBounds.minY);
		for (var stack : foundations) {
			foundationsBounds.union(stack.getBoundingBox());
		}
		return foundationsBounds;
	}
	
	private Rectanglef computeTableauBounds() {
		tableauBounds.setMin(tableauBounds.minX, tableauBounds.minY).setMax(tableauBounds.minX, tableauBounds.minY);
		for (var pile : tableau) {
			tableauBounds.union(pile.getBoundingBox());
		}
		return tableauBounds;
	}

	private void executeOperation(CardOperation operation) {
		operations.add(operation);
		operation.execute();
		
		if (operation.incrementsMoves()) {
			moveCounter++;
		}
		
		computeFoundationBounds();
		computeTableauBounds();
	}
	
	private void undoOperation() {
		if (operations.peekLast() != null) {
//			operations.peekLast().describe();
			CardOperation operation = operations.pollLast();
			operation.rollback();

			if (operation.incrementsMoves()) {
				moveCounter++;
			}
			
			computeFoundationBounds();
			computeTableauBounds();
		}
	}

	/**
	 * Returns an optional {@link CardOperation} for what cards to move when the {@link Stock} is clicked.
	 * 
	 * If the Stock is not empty the {@link CardOperation} will move up to the selected {@link DrawMode} number of cards into the {@link Waste}. In the case that the Stock is empty,
	 * but there are cards in the {@link Waste} the {@link CardOperation} will recycle the Waste cards into the Stock. If both the Stock and Waste are empty then the optional will
	 * also be empty.
	 * 
	 * @return A {@link CardOperation} for what cards to move when the {@link Stock} is clicked. 
	 */
	private Optional<CardOperation> getOperationForStockClick() {
		var stockCards = stock.getCardsToDraw();
		if (stockCards.isEmpty()) {
			if (waste.getCard().isPresent()) {
				return Optional.of(new CardMoveOperation(waste.getRecycleCards(), waste, stock, true));
			} else {
				return Optional.empty();
			}
		}
		
		return Optional.of(new CardMoveOperation(stockCards, stock, waste, true));
	}
	
	private Optional<CardOperation> getOperationForWasteClick() {
		var wasteCard = waste.getCard();
		if (wasteCard.isEmpty()) {
			return Optional.empty();
		}
		
		List<Card> wasteCardList = List.of(wasteCard.get());
		var destinationStack = getAutoDestinationStackForCards(wasteCardList);
		if (destinationStack.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(new CardMoveOperation(wasteCardList, waste, destinationStack.get()));
	}
	
	private Optional<Pair<Card, CardHolder>> getSelectedFoundation() {
		return Stream.of(foundations)
				.filter(foundation -> !foundation.isEmpty() && foundation.getBoundingBox().containsPoint(lastMouse))
				.map(foundation -> new Pair<Card, CardHolder>(foundation.getTopCard().get(), foundation))
				.findFirst();
	}
	
	private Optional<CardOperation> getOperationForFoundationClick() {
		var foundationCardAndStack = getSelectedFoundation();
		if (foundationCardAndStack.isEmpty()) {
			return Optional.empty();
		}
		
		List<Card> foundationCard = List.of(foundationCardAndStack.get().first());
		CardHolder foundationStack = foundationCardAndStack.get().second();
		var destinationStack = getAutoDestinationStackForCardsFrom(foundationCard, Arrays.stream(tableau));
		if (destinationStack.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(new CardMoveOperation(foundationCard, foundationStack, destinationStack.get()));
	}
	
	private Optional<Pair<List<Card>, CardHolder>> getSelectedTableau() {
		return Stream.of(tableau)
				.filter(pile -> !pile.isEmpty() && pile.getBoundingBox().containsPoint(lastMouse))
				.map(pile -> new Pair<List<Card>, CardHolder>(pile.getCardsFromPoint(lastMouse), pile))
				.findFirst();
	}
	
	private Optional<CardOperation> getOperationForTableauClick() {
		var tableauCardsAndPile = getSelectedTableau();
		if (tableauCardsAndPile.isEmpty()) {
			return Optional.empty();
		}
		
		List<Card> tableauCards = tableauCardsAndPile.get().first();
		CardHolder tableauPile = tableauCardsAndPile.get().second();
		var destinationStack = getAutoDestinationStackForCards(tableauCards);
		if (destinationStack.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(new CardMoveOperation(tableauCards, tableauPile, destinationStack.get()));
	}
	
	private Optional<CardStack> getAutoDestinationStackForCardsFrom(List<Card> cards, Stream<CardStack> streamOfDestinations) {
		return streamOfDestinations
				.filter(consumer -> consumer.canStack(cards))
				.findFirst();
	}
	
	private Optional<CardStack> getAutoDestinationStackForCards(List<Card> cards) {
		return getAutoDestinationStackForCardsFrom(cards, Stream.concat(Arrays.stream(foundations), Arrays.stream(tableau)));
	}
	
	private Optional<CardStack> getDestinationStackForCards(List<Card> cards) {
		return Stream.concat(Arrays.stream(foundations), Arrays.stream(tableau))
				.filter(consumer -> consumer.getBoundingBox().intersectsRectangle(hand.getBoundingBox()) && consumer.canStack(cards))
				.findFirst();
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
		
		stock.render(spriteRenderer, texture);
		waste.render(spriteRenderer, texture);
		for (Foundation foundation : foundations) {
			foundation.render(spriteRenderer, texture);
		}
		for (Pile pile : tableau) {
			pile.render(spriteRenderer, texture);
		}
		
		hand.render(spriteRenderer, texture);
		
		textRenderer.renderText(0, 0, "FPS: " + window.getFps());
		textRenderer.renderText(504 / 2f, 0, "Moves: " + moveCounter, uiFont);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		event.transformPoint(projection);
		lastMouse.set(event.mouseX, event.mouseY);
		
		if (stock.getBoundingBox().containsPoint(lastMouse)) {
			Optional<CardOperation> stockClickOperation = getOperationForStockClick();
			stockClickOperation.ifPresent(this::executeOperation);
		} else if (waste.getBoundingBox().containsPoint(lastMouse)) {
			Optional<CardOperation> wasteClickOperation = getOperationForWasteClick();
			wasteClickOperation.ifPresent(this::executeOperation);
		} else if (foundationsBounds.containsPoint(lastMouse)) {
			Optional<CardOperation> foundationClickOperation = getOperationForFoundationClick();
			foundationClickOperation.ifPresent(this::executeOperation);
		} else if (tableauBounds.containsPoint(lastMouse)) {
			Optional<CardOperation> tableauClickOperation = getOperationForTableauClick();
			tableauClickOperation.ifPresent(this::executeOperation);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		event.transformPoint(projection);
		lastMouse.set(event.mouseX, event.mouseY);
		
		if (waste.getBoundingBox().containsPoint(lastMouse)) {
			waste.getCard().ifPresent(card -> hand.holdCard(card, waste));
		} else if (foundationsBounds.containsPoint(lastMouse)) {
			getSelectedFoundation().ifPresent(hand::holdCard);
		} else if (tableauBounds.containsPoint(lastMouse)) {
			getSelectedTableau().ifPresent(hand::holdCards);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		if (hand.isEmpty()) {
			return;
		}
		
		Optional<CardStack> destinationStack = getDestinationStackForCards(hand.getCards());
		if (destinationStack.isEmpty() || destinationStack.get() == hand.getSupplier()) {
			hand.removeCards();
			return;
		}
		
		CardOperation placeCardsOperation = hand.placeCards(destinationStack.get());
		executeOperation(placeCardsOperation);
	}
	
	@Override
	public void mouseMoved(MouseEvent event) {
		// Not Implemented
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		event.transformPoint(projection);
		if (!hand.isEmpty()) {
			float x = event.mouseX;
			float y = event.mouseY;
			hand.move(x - lastMouse.x, y - lastMouse.y);
			
			lastMouse.set(x, y);
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// Not Implemented
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (!hand.isEmpty()) {
			// Don't perform undo operations while holding a card, likely unnecessary for setupTable but good to avoid for now.
			return;
		}
		switch (event.key) {
			case GLFW.GLFW_KEY_Z -> undoOperation();
			case GLFW.GLFW_KEY_Q -> setupTable();
			case GLFW.GLFW_KEY_E -> setupTable(random.nextLong());
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
