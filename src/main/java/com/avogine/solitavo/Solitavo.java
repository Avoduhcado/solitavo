package com.avogine.solitavo;

import com.avogine.Avogine;
import com.avogine.game.Game;
import com.avogine.io.Window;
import com.avogine.io.config.WindowPreferences;
import com.avogine.solitavo.controller.KlondikeController;
import com.avogine.solitavo.render.KlondikeRender;
import com.avogine.solitavo.scene.KlondikeScene;

/**
 *
 */
public class Solitavo implements Game {

	/**
	 * Entry point of the entire application.
	 * @param args
	 */
	public static void main(String[] args) {
		var solitavo = new Solitavo();
		var avogine = new Avogine(
				new Window("Solitaire", new WindowPreferences(504, 500, false, 0, false, 60, 15)),
				solitavo);
		avogine.start();
	}
	
	private final KlondikeScene scene;
	private final KlondikeRender render;
	private final KlondikeController controller;
	
	private Window cachedWindow;
	
	/**
	 * 
	 */
	public Solitavo() {
		scene = new KlondikeScene();
		render = new KlondikeRender();
		controller = new KlondikeController();
	}

	@Override
	public void init(Window window) {
		render.init(window);
		render.setupData(scene);
		controller.init(this, window);
		cachedWindow = window;
	}
	
	@Override
	public void input(Window window) {
//		if (gui != null) {
//			gui.inputBegin();
//		}
//		
//		window.pollEvents();
//		
//		if (gui != null) {
//			gui.inputEnd();
//		}
	}

	@Override
	public void update(float interval) {
		// TODO add a timer
		controller.update(interval);
	}

	@Override
	public void render(Window window) {
		render.render(window, scene);
	}

	@Override
	public void cleanup() {
		scene.cleanup();
		render.cleanup();
	}
	
	/**
	 * @return the scene
	 */
	public KlondikeScene getScene() {
		return scene;
	}
	
}
