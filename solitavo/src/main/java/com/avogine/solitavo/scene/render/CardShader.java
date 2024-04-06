package com.avogine.solitavo.scene.render;

import com.avogine.render.shader.ShaderProgram;

/**
 *
 */
public class CardShader extends ShaderProgram {

	/**
	 * 
	 */
	public CardShader() {
		super("basicVertex.glsl", "basicFragment.glsl");
	}
	
}
