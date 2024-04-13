package com.avogine.solitavo.game;

import com.avogine.game.Game;
import com.avogine.game.ui.nuklear.NuklearUI;
import com.avogine.io.*;
import com.avogine.solitavo.scene.wild.WildScene;

/**
 *
 */
public class SolitearGame extends Game {
	
	/**
	 * 
	 */
	public SolitearGame() {
		super();
		setScene(new WildScene());
	}
	
	@Override
	public void init(Window window, Audio audio, NuklearUI gui) {
		super.init(window, audio, gui);
		getCurrentScene().init(this, getWindow());
	}

}
