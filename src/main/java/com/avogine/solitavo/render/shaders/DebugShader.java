package com.avogine.solitavo.render.shaders;

import com.avogine.render.shader.ShaderProgram;
import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class DebugShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformVec4 lineColor = new UniformVec4();
	
	/**
	 * 
	 */
	public DebugShader() {
		super("debugVertex.glsl", "debugFragment.glsl");
		storeAllUniformLocations(projection, model, lineColor);
	}
	
}
