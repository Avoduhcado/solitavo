package com.avogine.solitavo;

import com.avogine.Avogine;
import com.avogine.io.Window;
import com.avogine.solitavo.game.SolitearGame;

/**
 *
 */
public class Solitavo {

	public static void main(String[] args) {
		var avogine = new Avogine(new Window(504, 500, "Solitaire"), new SolitearGame());
		avogine.start();
	}
	
}
