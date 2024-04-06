package com.avogine.solitavo.game;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;
import java.util.Optional;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.game.Game;
import com.avogine.game.util.*;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;
import com.avogine.solitavo.scene.klondike.KlondikeScene;
import com.avogine.solitavo.scene.klondike.entity.Card;

/**
 * TODO Add getPositionOnCard method to offset click moves
 */
public class CardUpdater implements MouseMotionListener, MouseClickListener, MouseScrollListener, Updateable {

	private float firstX;
	private float firstY;
	private float lastX;
	private float lastY;
	
	private Card heldCard;
	private boolean cardsMoved = true;
	
	private long windowId;
	
	private KlondikeScene scene;
	
	@Override
	public void onRegister(Game game) {
		if (game.getCurrentScene() instanceof KlondikeScene klondike) {
			this.scene = klondike;
		}
		game.addInputListener(this);
		windowId = game.getWindow().getId();
	}

	@Override
	public void onUpdate(GameState gameState) {
//		if (!cardsMoved) {
//			return;
//		}
//		Arrays.sort(scene.getDeck(), (card1, card2) -> {
//			if (card1 == heldCard) {
//				return 1;
//			} else {
//				return (int) (card1.getPosition().y - card2.getPosition().y);
//			}
//		});
//		cardsMoved = false;
	}

	@Override
	public void mouseMoved(MouseMotionEvent event) {
		Vector2f transformedMouse = transformMouse(event.xPosition(), event.yPosition());
		lastX = transformedMouse.x();
		lastY = transformedMouse.y();
		if (heldCard != null) {
			heldCard.setPosition(new Vector2f(lastX + heldCard.getClickOffset().x, lastY + heldCard.getClickOffset().y));
			cardsMoved = true;
		}
	}

	@Override
	public void mouseClicked(MouseClickEvent event) {
		Vector2f transformedMouse = transformMouse(event.mouseX(), event.mouseY());
		switch (event.button()) {
			case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
				if (heldCard != null) {
					heldCard.setScale(1f);
					heldCard = null;
				} else {
					Optional<Card> firstClickCard = scene.getCardAt(firstX, firstY);
					Optional<Card> lastClickCard = scene.getCardAt(transformedMouse.x, transformedMouse.y);
					if (firstClickCard.equals(lastClickCard)) {
						lastClickCard.ifPresent(card -> {
							heldCard = card;
							heldCard.setScale(1.1f);
							heldCard.setClickOffset(transformedMouse.x, transformedMouse.y);
						});
					}
				}
			}
			case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
				if (heldCard != null) {
					heldCard.setFaceUp(!heldCard.isFaceUp());
				}
			}
		}
	}
	
	@Override
	public void mousePressed(MouseClickEvent event) {
		Vector2f transformedMouse = transformMouse(event.mouseX(), event.mouseY());
		firstX = transformedMouse.x();
		firstY = transformedMouse.y();
	}
	
	@Override
	public void mouseReleased(MouseClickEvent event) {
		// No implementation
	}

	@Override
	public void mouseScrolled(MouseScrollEvent event) {
		if (heldCard != null) {
			heldCard.setRotation(heldCard.getRotation() - event.yOffset());
		}
	}
	
	private Vector2f transformMouse(float mouseX, float mouseY) {
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
		Vector3f transformedMouse = scene.getProjection()
				.invertOrtho(new Matrix4f())
				.transformPosition(mouseX, -mouseY, 0, new Vector3f())
				.div(halfWidth, halfHeight, 1f);
		return new Vector2f(transformedMouse.x, transformedMouse.y);
	}
	
}
