package com.avogine.solitavo.scene.render;

import com.avogine.render.shader.ShaderProgram;
import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class SpriteShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformSampler spriteTexture = new UniformSampler();
	public final UniformVec4 textureOffset = new UniformVec4();
	
	/**
	 * @param vertexShader
	 * @param fragmentShader
	 */
	public SpriteShader(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
		storeAllUniformLocations(projection, model, spriteTexture, textureOffset);
		loadTextureUnit();
	}
	
	private void loadTextureUnit() {
		bind();
		spriteTexture.loadTexUnit(0);
		unbind();
	}
	
}
