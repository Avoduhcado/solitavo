package com.avogine.solitavo.scene.klondike;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
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
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.*;
import java.util.Random;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.io.Window;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.render.data.TextureAtlas;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.solitavo.game.CardUpdater;
import com.avogine.solitavo.scene.klondike.command.CardOperation;
import com.avogine.solitavo.scene.klondike.entity.*;
import com.avogine.solitavo.scene.render.SpriteRenderer;

/**
 * TODO add an onUpdate
 */
public class KlondikeScene extends Scene implements MouseMotionListener, MouseClickListener, MouseScrollListener {

	/**
	 * 
	 */
	public static final Vector2f CARD_SIZE = new Vector2f(72f, 100f);
	
	private SpriteRenderer spriteRenderer;
	
	private TextureAtlas texture;
	
	private final Stock stock;
	private final Waste waste;
	private final List<Foundation> foundations;
	private final List<Cascade> tableau;
	
	private final List<Card> heldCards;
	private CardStack previousStack;
	
	private Card[] bonus;
	private int cardBackIndex;
	
	private final Random random;
	
	private long windowId;
	private float firstX;
	private float firstY;
	
	/**
	 * 
	 */
	public KlondikeScene() {
		projection.setOrtho2D(0, 504, 500, 0);
		spriteRenderer = new SpriteRenderer();
		cardBackIndex = 4;

		waste = new Waste();
		stock = new Stock(new Vector2f(0f, 0f), waste, 1, this);
		tableau = new ArrayList<>();
		foundations = new ArrayList<>();
		
		heldCards = new ArrayList<>();
		
		random = new Random();
	}
	
	@Override
	public void init(Game game, Window window) {
		windowId = window.getId();
		game.register(new CardUpdater());
		game.register(spriteRenderer);
		
		game.addInputListener(this);
		
		texture = TextureCache.getInstance().getTextureAtlas("Cardsheet.png", Rank.values().length, Suit.values().length);
		CARD_SIZE.set((float) texture.getWidth() / texture.getColumns(), (float) texture.getHeight() / texture.getRows());
		
		for (int i = 0; i < 52; i++) {
			stock.addCard(new Card(new Vector2f(), CARD_SIZE, 0, 1, i % Rank.values().length, i / Rank.values().length));
		}
		Collections.shuffle(stock.getCards(), random);

		for (int i = 0; i < 4; i++) {
			foundations.add(new Foundation(new Vector2f(72f * 3 + 72f * i, 0f)));
		}
		
		for (int i = 0; i < 7; i++) {
			tableau.add(new Cascade(new Vector2f(i * 72f, 100f), this));
		}
		
		for (int i = 0; i < tableau.size(); i++) {
			for (int j = i; j < 7; j++) {
				tableau.get(j).addCard(stock.removeFirst());
			}
			tableau.get(i).getLast().setFaceUp(true);
		}
		
		bonus = new Card[13];
		for (int i = 0; i < bonus.length; i++) {
			bonus[i] = new Card(new Vector2f(CARD_SIZE.x * i, 0), CARD_SIZE, 0, 1, i, Suit.BONUS.ordinal());
		}
	}

	@Override
	public void onRender(Window window) {
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		
		glClearColor(36f / 255f, 115f / 255f, 69f / 255f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		spriteRenderer.drawSprite(
				new Vector2f(stock.getBounds().minX, stock.getBounds().minY),
				CARD_SIZE,
				0f, 1f, texture.getId(),
				new Vector4f(
						(float) (stock.getCards().isEmpty() ? Rank.THREE.ordinal() : cardBackIndex) / Rank.values().length,
						(float) Suit.BONUS.ordinal() / Suit.values().length,
						1f / texture.getColumns(), 1f / texture.getRows()));
		
		
		waste.getCards().forEach(card -> spriteRenderer.drawSprite(
				card.getPosition(), card.getSize(),
				0f, 1f, texture.getId(),
				new Vector4f(
						(float) card.getRank() / Rank.values().length,
						(float) card.getSuit() / Suit.values().length,
						1f / texture.getColumns(), 1f / texture.getRows()))
				);
		
		foundations.forEach(stack -> spriteRenderer.drawSprite(
				new Vector2f(stack.getBounds().minX, stack.getBounds().minY), CARD_SIZE,
				0f, 1f, texture.getId(),
				new Vector4f(
						(float) (stack.isEmpty() ? Rank.KING.ordinal() : stack.getLast().getRank()) / Rank.values().length,
						(float) (stack.isEmpty() ? Suit.BONUS.ordinal() : stack.getLast().getSuit()) / Suit.values().length,
						1f / texture.getColumns(), 1f / texture.getRows()))
				);
		
		tableau.forEach(cascade -> {
			for (int i = 0; i < cascade.getCards().size(); i++) {
				var card = cascade.get(i);
				spriteRenderer.drawSprite(
						card.getPosition(),
						card.getSize(),
						0, 1, texture.getId(), 
						new Vector4f(
								(float) (card.isFaceUp() ? card.getRank() : cardBackIndex) / Rank.values().length,
								(float) (card.isFaceUp() ? card.getSuit() : Suit.BONUS.ordinal()) / Suit.values().length,
								1f / texture.getColumns(), 1f / texture.getRows()));
			}
		});

		heldCards.forEach(card -> spriteRenderer.drawSprite(
				card.getPosition(),
				card.getSize(),
				0f, 1f, texture.getId(),
				new Vector4f(
						(float) card.getRank() / Rank.values().length,
						(float) card.getSuit() / Suit.values().length,
						1f / texture.getColumns(), 1f / texture.getRows()))
				);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
	}
	
	@Override
	public void onUpdate(float delta) {
		// TODO Card movement
	}
	
	/**
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Optional<Card> getCardAt(float mouseX, float mouseY) {
		List<Card> selections = new ArrayList<>();
//		for (Card card : getCards()) {
//			if (card.getBoundingBox().containsPoint(mouseX, mouseY)) {
//				selections.add(card);
//			}
//		}
		
		return Optional.ofNullable(selections.isEmpty() ? null : selections.getLast());
	}
	
	@Override
	public void mouseMoved(MouseMotionEvent event) {
//		Vector2f transformedMouse = transformMouse(event.xPosition(), event.yPosition());
//		if (!heldCards.isEmpty()) {
//			heldCards.forEach(card -> card.setPosition(new Vector2f(
//					transformedMouse.x() + card.getClickOffset().x,
//					transformedMouse.y() + card.getClickOffset().y)));
//		}
		// TODO Scale for transformed screen size
		heldCards.forEach(card -> card.getPosition().add(event.xDelta(), -event.yDelta()));
	}

	private Optional<Foundation> getFoundationClicked(Vector2f point) {
		return foundations.stream().filter(stack -> stack.containsPoint(point)).findFirst();
	}
	
	private Optional<Cascade> getCascadeClicked(Vector2f point) {
		return tableau.stream().filter(cascade -> cascade.containsPoint(point)).findFirst();
	}

	@Override
	public void mouseClicked(MouseClickEvent event) {
		if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return;
		}
		
		Vector2f transformedMouse = transformMouse(event.mouseX(), event.mouseY());
		if (stock.containsPoint(transformedMouse)) {
			stock.clicked(transformedMouse, heldCards, previousStack);
			previousStack = stock;
		} else if (waste.containsPoint(transformedMouse)) {
			waste.clicked(transformedMouse, heldCards, previousStack);
			previousStack = waste;
		} else {
//			getFoundationClicked(transformedMouse).ifPresentOrElse(
//					foundation -> {
//						foundation.clicked(transformedMouse, heldCards, previousStack);
//						previousStack = foundation;
//					},
//					() -> getCascadeClicked(transformedMouse).ifPresentOrElse(
//							cascade -> {
//								cascade.clicked(transformedMouse, heldCards, previousStack);
//								previousStack = cascade;
//							},
//							() -> {
//								if (previousStack != null) {
//									previousStack.reset(heldCards);
//								}
//							})
//					);
		}
	}
	
	@Override
	public void mousePressed(MouseClickEvent event) {
		if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT || !heldCards.isEmpty()) {
			return;
		}
		
		Vector2f transformedMouse = transformMouse(event.mouseX(), event.mouseY());
//		if (stock.containsPoint(transformedMouse)) {
//			stock.clicked(transformedMouse, heldCards, previousStack);
//			previousStack = stock;
//		} else if (waste.containsPoint(transformedMouse)) {
//			waste.clicked(transformedMouse, heldCards, previousStack);
//			previousStack = waste;
//		} else {
			getFoundationClicked(transformedMouse).ifPresentOrElse(
					foundation -> {
						foundation.clicked(transformedMouse, heldCards, previousStack);
						previousStack = foundation;
					},
					() -> getCascadeClicked(transformedMouse).ifPresentOrElse(
							cascade -> {
								cascade.grab(transformedMouse, heldCards);
//								previousStack = cascade;
							},
							() -> {
//								if (previousStack != null) {
//									previousStack.reset(heldCards);
//								}
							})
					);
//		}
	}
	
	@Override
	public void mouseReleased(MouseClickEvent event) {
		if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT || heldCards.isEmpty()) {
			return;
		}

		Vector2f transformedMouse = transformMouse(event.mouseX(), event.mouseY());
		getFoundationClicked(transformedMouse).ifPresentOrElse(
				foundation -> {
					if (foundation.isStackable(heldCards)) {
						foundation.addAll(heldCards);
						heldCards.clear();
					}
					foundation.clicked(transformedMouse, heldCards, previousStack);
					previousStack = foundation;
				},
				() -> getCascadeClicked(transformedMouse).ifPresent(
						cascade -> {
							if (cascade.isStackable(heldCards)) {
								cascade.addCards(heldCards);
								heldCards.clear();
							}
						})
				);
		
		heldCards.forEach(Card::reset);
		heldCards.clear();
	}

	@Override
	public void mouseScrolled(MouseScrollEvent event) {
//		if (heldCards != null) {
//			heldCards.setRotation(heldCards.getRotation() - event.yOffset());
//		}
	}
	
	/**
	 * @param operation
	 */
	public void executeOperation(CardOperation operation) {
		operation.execute();
	}
	
	/**
	 * TODO Extract to some utility that also takes in a projection?
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Vector2f transformMouse(float mouseX, float mouseY) {
		int displayWidth;
		int displayHeight;
		float halfWidth;
		float halfHeight;
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

			glfwGetFramebufferSize(windowId, w, h);
			displayWidth = w.get(0);
			displayHeight = h.get(0);

			halfWidth = displayWidth / 2.0f;
			halfHeight = displayHeight / 2.0f;
		}
		Vector3f transformedMouse = getProjection()
				.invertOrtho(new Matrix4f())
				.transformPosition(mouseX, -mouseY, 0, new Vector3f())
				.div(halfWidth, halfHeight, 1f);
		return new Vector2f(transformedMouse.x, transformedMouse.y);
	}

}
