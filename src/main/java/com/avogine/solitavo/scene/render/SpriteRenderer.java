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
import com.avogine.render.data.TextureAtlas;

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
	 */
	public void renderSprite(Vector2f position, Vector2f size, float rotation, float scale, int textureID) {
		spriteShader.bind();
		
		spriteShader.atlasCellDimensions.loadVec2(1, 1);
		spriteShader.atlasCoordinates.loadVec2(1, 1);
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(position.x, position.y, 0);
		
		modelMatrix.translate(size.x * 0.5f, size.y * 0.5f, 0);
		modelMatrix.rotateZ((float) Math.toRadians(rotation));
		modelMatrix.translate(-size.x * 0.5f, -size.y * 0.5f, 0);
		
		modelMatrix.scale(size.x, size.y, 1f);
		
		modelMatrix.scaleAroundLocal(scale, position.x + (size.x * 0.5f), position.y + (size.y * 0.5f), 1f);
		spriteShader.model.loadMatrix(modelMatrix);
		
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
	 */
	public void renderSprite(Vector2f position, Vector2f size, int textureId) {
		renderSprite(position, size, 0, 1, textureId);
	}
	
	/**
	 * @param position
	 * @param size
	 * @param rotation 
	 * @param scale 
	 * @param textureAtlas
	 * @param column 
	 * @param row 
	 */
	public void renderSpriteAtlas(Vector2f position, Vector2f size, float rotation, float scale, TextureAtlas textureAtlas, int column, int row) {
		spriteShader.bind();

		spriteShader.atlasCellDimensions.loadVec2(textureAtlas.getCellWidth(), textureAtlas.getCellHeight());
		spriteShader.atlasCoordinates.loadVec2(column * textureAtlas.getCellWidth(), row * textureAtlas.getCellHeight());
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(position.x, position.y, 0);
		
		modelMatrix.translate(size.x * 0.5f, size.y * 0.5f, 0);
		modelMatrix.rotateZ((float) Math.toRadians(rotation));
		modelMatrix.translate(-size.x * 0.5f, -size.y * 0.5f, 0);
		
		modelMatrix.scale(size.x, size.y, 1f);
		
		modelMatrix.scaleAroundLocal(scale, position.x + (size.x * 0.5f), position.y + (size.y * 0.5f), 1f);
		spriteShader.model.loadMatrix(modelMatrix);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureAtlas.id());
		
		glBindVertexArray(spriteVao);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindVertexArray(0);
		
		spriteShader.unbind();
	}
	
	/**
	 * @param position
	 * @param size
	 * @param textureAtlas
	 * @param column
	 * @param row
	 */
	public void renderSpriteAtlas(Vector2f position, Vector2f size, TextureAtlas textureAtlas, int column, int row) {
		renderSpriteAtlas(position, size, 0f, 1f, textureAtlas, column, row);
	}

	@Override
	public void onCleanup() {
		if (spriteShader != null) {
			spriteShader.cleanup();
		}
		glDeleteVertexArrays(spriteVao);
	}

}
