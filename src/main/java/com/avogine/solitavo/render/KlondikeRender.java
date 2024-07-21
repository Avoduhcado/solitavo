package com.avogine.solitavo.render;

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
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL14C.*;

import org.joml.*;

import com.avogine.io.Window;
import com.avogine.render.*;
import com.avogine.render.data.*;
import com.avogine.render.loader.font.FontCache;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.solitavo.scene.*;
import com.avogine.solitavo.scene.cards.*;
import com.avogine.solitavo.scene.klondike.*;

/**
 *
 */
public class KlondikeRender implements SceneRender<KlondikeScene> {

	private SpriteRender spriteRender;
	private TextRender textRender;
	private DebugRender debugRender;
	
	private final Vector4f debugStockColor;
	private final Vector4f debugWasteColor;
	private final Vector4f debugFoundationsColor;
	private final Vector4f debugTableauColor;
	private final Vector4f debugHandColor;
	
	private TextureAtlas texture;
	private FontDetails uiFont;
	
	/**
	 * 
	 */
	public KlondikeRender() {
		spriteRender = new SpriteRender();
		textRender = new TextRender();
		debugRender = new DebugRender();
		
		debugStockColor = new Vector4f(0f, 0f, 1f, 1f);
		debugWasteColor = new Vector4f(1f, 0f, 1f, 1f);
		debugFoundationsColor = new Vector4f(0f, 1f, 1f, 1f);
		debugTableauColor = new Vector4f(0.5f, 0.5f, 0.5f, 1f);
		debugHandColor = new Vector4f(0f, 0f, 0f, 1f);
	}
	
	@Override
	public void init() {
		SceneRender.super.init();

		glEnable(GL_MULTISAMPLE);
		
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glClearColor(36f / 255f, 115f / 255f, 69f / 255f, 1.0f);
	}
	
	/**
	 * Configure renderers and cache texture and font resources.
	 * @param scene
	 */
	public void setupData(KlondikeScene scene) {
		Matrix4f projection = scene.getProjection();
		spriteRender.init(projection);
		textRender.init(projection);
		debugRender.init(projection);
		
		texture = TextureCache.getInstance().getTextureAtlas("Cardsheet.png", Rank.values().length, Suit.values().length);
		uiFont = FontCache.getInstance().getFont("alagard.ttf");
	}

	@Override
	public void render(Window window, KlondikeScene scene) {
		glClear(GL_COLOR_BUFFER_BIT);
		
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		
		scene.getStock().render(spriteRender, texture);
		scene.getWaste().render(spriteRender, texture);
		for (Foundation foundation : scene.getFoundations()) {
			foundation.render(spriteRender, texture);
		}
		for (Pile pile : scene.getTableau()) {
			pile.render(spriteRender, texture);
		}
		scene.getHand().render(spriteRender, texture);
		
		if (window.isDebugMode()) {
			debugRender(scene);
		}
		
		textRender.renderText(0, 0, "FPS: " + window.getFps());
		textRender.renderText(504 / 2f, 0, "Moves: " + scene.getMoveCounter(), uiFont);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}
	
	private void debugRender(KlondikeScene scene) {
		debugRender.renderRect(scene.getStock().getBoundingBox(), debugStockColor);
		debugRender.renderRect(scene.getWaste().getBoundingBox(), debugWasteColor);
		for (Foundation foundation : scene.getFoundations()) {
			debugRender.renderRect(foundation.getBoundingBox(), debugFoundationsColor);
		}
		debugRender.renderRect(scene.getFoundationsBounds(), debugFoundationsColor);
		for (Pile pile : scene.getTableau()) {
			debugRender.renderRect(pile.getBoundingBox(), debugTableauColor);
		}
		debugRender.renderRect(scene.getTableauBounds(), debugFoundationsColor);
		debugRender.renderRect(scene.getHand().getBoundingBox(), debugHandColor);
	}
	
	@Override
	public void cleanup() {
		spriteRender.cleanup();
		textRender.cleanup();
		debugRender.cleanup();
		if (texture != null) {
			texture.cleanup();
		}
		if (uiFont != null) {
			uiFont.cleanup();
		}
	}

}
