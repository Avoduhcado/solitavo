package com.avogine.solitavo.controller;

import java.util.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.joml.*;
import org.joml.primitives.Rectanglef;
import org.lwjgl.glfw.GLFW;

import com.avogine.io.Window;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.solitavo.Solitavo;
import com.avogine.solitavo.scene.*;
import com.avogine.solitavo.scene.cards.*;
import com.avogine.solitavo.scene.command.*;
import com.avogine.solitavo.scene.klondike.*;
import com.avogine.solitavo.scene.klondike.Stock.DrawMode;
import com.avogine.solitavo.scene.util.*;
import com.avogine.util.Pair;

/**
 *
 */
public class KlondikeController implements MouseButtonListener, MouseMotionListener, KeyListener {

	private final Vector2f lastMouse = new Vector2f();

	private final ArrayDeque<CardOperation> operations = new ArrayDeque<>();

	private final Random random = new Random();
	private long seed;
	
	private Matrix4f projection;
	private Stock stock;
	private Waste waste;
	private Foundation[] foundations;
	private Pile[] tableau;
	private Hand hand;
	private Rectanglef foundationsBounds;
	private Rectanglef tableauBounds;
	private AtomicInteger moveCounter;
	
	/**
	 * @param game 
	 * @param window
	 */
	public void init(Solitavo game, Window window) {
		KlondikeScene scene = game.getScene();
		projection = scene.getProjection();
		stock = scene.getStock();
		waste = scene.getWaste();
		foundations = scene.getFoundations();
		tableau = scene.getTableau();
		hand = scene.getHand();
		foundationsBounds = scene.getFoundationsBounds();
		tableauBounds = scene.getTableauBounds();
		moveCounter = scene.getMoveCounter();
		
		window.getInput().addInputListener(this);
		
		setupTable(random.nextLong());
	}
	
	private void setupTable(long seed) {
		this.seed = seed;
		random.setSeed(seed);
		
		setupTable();
	}
	
	private void setupTable() {
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < 52; i++) {
			int rankIndex = i % Rank.values().length;
			int suitIndex = i / Rank.values().length;
			cards.add(new Card(new Vector2f(), Rank.values()[rankIndex], Suit.values()[suitIndex]));
		}
		random.setSeed(seed);
		Collections.shuffle(cards, random);
		
		stock.init();
		stock.addCards(cards);
		
		waste.init();
		
		for (var foundation : foundations) {
			foundation.init();
		}
		foundationsBounds.set(foundations[0].getBoundingBox());
		computeFoundationBounds();
		
		for (var pile : tableau) {
			pile.init();
		}
		for (int x = 0; x < 7; x++) {
			for (int y = x; y < 7; y++) {
				tableau[y].dealCard(stock.getCards().removeLast());
			}
			tableau[x].revealTopCard();
		}
		tableauBounds.set(tableau[0].getBoundingBox());
		computeTableauBounds();
		
		hand.init();
		
		moveCounter.set(0);
		operations.clear();
	}
	
	private void executeOperation(CardOperation operation) {
		operations.add(operation);
		operation.execute();
		
		if (operation.incrementsMoves()) {
			moveCounter.incrementAndGet();
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
				moveCounter.incrementAndGet();
			}
			
			computeFoundationBounds();
			computeTableauBounds();
		}
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
			// TODO Put this in a menu so you can't just change it mid game
			case GLFW.GLFW_KEY_1 -> stock.setDrawMode(stock.getDrawMode()== DrawMode.STANDARD ? DrawMode.SINGLE : DrawMode.STANDARD);
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
