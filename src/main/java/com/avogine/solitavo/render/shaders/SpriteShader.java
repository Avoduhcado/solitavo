package com.avogine.solitavo.render.shaders;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.shader.ShaderProgram;
import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class SpriteShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformSampler spriteTexture = new UniformSampler();
	public final UniformVec2 atlasCoordinates = new UniformVec2();
	public final UniformVec2 atlasCellDimensions = new UniformVec2();
	
	/**
	 * 
	 */
	public SpriteShader() {
		super(SHADERS.with("spriteVertex.glsl"), SHADERS.with("spriteFragment.glsl"));
		storeAllUniformLocations(projection, model, spriteTexture, atlasCoordinates, atlasCellDimensions);
		loadTextureUnit();
	}
	
	private void loadTextureUnit() {
		bind();
		spriteTexture.loadTexUnit(0);
		unbind();
	}
	
}
