package com.avogine.solitavo.scene.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.Math;

import org.joml.*;

import com.avogine.game.Game;
import com.avogine.game.util.Cleanupable;

/**
 *
 */
public class SpriteRenderer implements Cleanupable {

	private SpriteShader spriteShader;
	
	private int spriteVao;

	@Override
	public void onRegister(Game game) {
		init(game.getCurrentScene().getProjection());
	}

	/**
	 * @param projection
	 */
	private void init(Matrix4f projection) {
		spriteShader = new SpriteShader("spriteVertex.glsl", "spriteFragment.glsl");
		spriteShader.bind();
		spriteShader.projection.loadMatrix(projection);
		spriteShader.unbind();
		
		// Card specific texture offsets
		var spriteVertices = new float[] {
				// pos		// tex
				0.0f, 1.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 0.0f, 

				0.0f, 1.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 0.0f, 1.0f, 0.0f
		};

		spriteVao = glGenVertexArrays();
		
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, spriteVertices, GL_STATIC_DRAW);
		
		glBindVertexArray(spriteVao);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	/**
	 * @param position
	 * @param size
	 * @param rotation
	 * @param scale Apply scaling from center of sprite.
	 * @param textureID
	 * @param textureOffset
	 */
	public void drawSprite(Vector2f position, Vector2f size, float rotation, float scale, int textureID, Vector4f textureOffset) {
		spriteShader.bind();
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(position.x, position.y, 0);
		
		modelMatrix.translate(size.x * 0.5f, size.y * 0.5f, 0);
		modelMatrix.rotateZ((float) Math.toRadians(rotation));
		modelMatrix.translate(-size.x * 0.5f, -size.y * 0.5f, 0);
		
		modelMatrix.scale(size.x, size.y, 1f);
		
		modelMatrix.scaleAroundLocal(scale, position.x + (size.x * 0.5f), position.y + (size.y * 0.5f), 1f);
		spriteShader.model.loadMatrix(modelMatrix);
		spriteShader.textureOffset.loadVec4(textureOffset);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glBindVertexArray(spriteVao);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindVertexArray(0);
		
		spriteShader.unbind();
	}
	
	/**
	 * @param position
	 * @param size
	 * @param textureId
	 * @param textureOffset
	 */
	public void drawSprite(Vector2f position, Vector2f size, int textureId, Vector4f textureOffset) {
		drawSprite(position, size, 0, 1, textureId, textureOffset);
	}

	@Override
	public void onCleanup() {
		if (spriteShader != null) {
			spriteShader.cleanup();
		}
		glDeleteVertexArrays(spriteVao);
	}

}
