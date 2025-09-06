package com.avogine.solitavo.render.shaders;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.opengl.shader.ShaderProgram;
import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class SpriteShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformSampler spriteSheet = new UniformSampler();
	
	/**
	 * 
	 */
	public SpriteShader() {
		super(SHADERS.with("spriteVertex.glsl"), SHADERS.with("spriteFragment.glsl"));
		storeAllUniformLocations(projection, model, spriteSheet);
		loadTextureUnit();
	}
	
	private void loadTextureUnit() {
		bind();
		spriteSheet.loadTexUnit(0);
		unbind();
	}
	
}
