package com.avogine.solitavo.scene.render;

import com.avogine.render.shader.ShaderProgram;

/**
 *
 */
public class TableShader extends ShaderProgram {

	/**
	 * 
	 */
	public TableShader() {
		super("basicVertex.glsl", "basicFragment.glsl");
	}
	
}
