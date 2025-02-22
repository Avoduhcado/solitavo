package com.avogine.solitavo.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.*;
import org.joml.Math;
import org.joml.primitives.Rectanglef;
import org.lwjgl.system.MemoryStack;

import com.avogine.render.data.texture.Texture;
import com.avogine.solitavo.render.data.TextureAtlas;
import com.avogine.solitavo.render.shaders.SpriteShader;

/**
 *
 */
public class SpriteRender {

	private SpriteShader spriteShader;
	
	private int spriteVao;
	private int spritePositionVbo;
	private int spriteTextureCoordinatesVbo;
	private int spriteIndicesVbo;
	
	private boolean atlasModified = false;
	
	private final Matrix4f modelMatrix;
	
	private final Rectanglef atlasCell;
	
	/**
	 * 
	 */
	public SpriteRender() {
		modelMatrix = new Matrix4f();
		atlasCell = new Rectanglef();
	}
	
	/**
	 * @param projection
	 */
	public void init(Matrix4f projection) {
		spriteShader = new SpriteShader();
		spriteShader.bind();
		spriteShader.projection.loadMatrix(projection);
		spriteShader.unbind();
		
		var spritePos = new float[] {
				0.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 0.0f,
				1.0f, 1.0f
		};
		// Winding order CCW, starting bottom left
		var spriteIndices = new int[] {
				0, 1, 2,
				0, 3, 1
		};
		
		spriteVao = glGenVertexArrays();
		glBindVertexArray(spriteVao);
		
		// TODO This could potentially still be 1 VBO of XYXYXYXYXYXYUVUVUVUVUVUV and 2 vertex attrib's with a stride of 2, pointer of 12 for texture coordinates, then just subBuffer that second half
		spritePositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, spritePositionVbo);
		glBufferData(GL_ARRAY_BUFFER, spritePos, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		spriteTextureCoordinatesVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, spriteTextureCoordinatesVbo);
		glBufferData(GL_ARRAY_BUFFER, spritePos, GL_DYNAMIC_DRAW);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		spriteIndicesVbo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, spriteIndicesVbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, spriteIndices, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	/**
	 * @param position
	 * @param size
	 * @param rotation
	 * @param scale Apply scaling from center of sprite.
	 * @param texture
	 */
	public void renderSprite(Vector2f position, Vector2f size, float rotation, float scale, Texture texture) {
		spriteShader.bind();
		
		modelMatrix.identity();
		modelMatrix.translate(position.x, position.y, 0);
		
		modelMatrix.translate(size.x * 0.5f, size.y * 0.5f, 0);
		modelMatrix.rotateZ(Math.toRadians(rotation));
		modelMatrix.translate(-size.x * 0.5f, -size.y * 0.5f, 0);
		
		modelMatrix.scale(size.x, size.y, 1f);
		
		modelMatrix.scaleAroundLocal(scale, position.x + (size.x * 0.5f), position.y + (size.y * 0.5f), 1f);
		spriteShader.model.loadMatrix(modelMatrix);
		
		bindSpriteTexture(texture);
		
		glBindVertexArray(spriteVao);
		if (atlasModified) {
			glBindBuffer(GL_ARRAY_BUFFER, spriteTextureCoordinatesVbo);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer atlasOffsets = stack.floats(0f, 1f, 1f, 0f, 0f, 0f, 1f, 1f);
				glBufferSubData(GL_ARRAY_BUFFER, 0, atlasOffsets);
			}
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			atlasModified = false;
		}
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
		
		spriteShader.unbind();
	}
	
	/**
	 * @param position
	 * @param size
	 * @param texture
	 */
	public void renderSprite(Vector2f position, Vector2f size, Texture texture) {
		renderSprite(position, size, 0, 1, texture);
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
		
		modelMatrix.identity();
		modelMatrix.translate(position.x, position.y, 0);
		
		modelMatrix.translate(size.x * 0.5f, size.y * 0.5f, 0);
		modelMatrix.rotateZ(Math.toRadians(rotation));
		modelMatrix.translate(-size.x * 0.5f, -size.y * 0.5f, 0);
		
		modelMatrix.scale(size.x, size.y, 1f);
		
		modelMatrix.scaleAroundLocal(scale, position.x + (size.x * 0.5f), position.y + (size.y * 0.5f), 1f);
		spriteShader.model.loadMatrix(modelMatrix);
		
		bindSpriteTexture(textureAtlas.texture());
		
		glBindVertexArray(spriteVao);
		glBindBuffer(GL_ARRAY_BUFFER, spriteTextureCoordinatesVbo);
		atlasCell.setMin(textureAtlas.cellX(column), textureAtlas.cellY(row)).setMax(textureAtlas.cellWidth(column), textureAtlas.cellHeight(row));
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer atlasOffsets = stack.floats(
					atlasCell.minX, atlasCell.maxY,
					atlasCell.maxX, atlasCell.minY,
					atlasCell.minX, atlasCell.minY,
					atlasCell.maxX, atlasCell.maxY
					);
			glBufferSubData(GL_ARRAY_BUFFER, 0, atlasOffsets);
			atlasModified = true;
		}
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
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
	
	private void bindSpriteTexture(Texture texture) {
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
	}

	/**
	 * 
	 */
	public void cleanup() {
		if (spriteShader != null) {
			spriteShader.cleanup();
		}
		glDeleteVertexArrays(spriteVao);
		glDeleteBuffers(spritePositionVbo);
		glDeleteBuffers(spriteTextureCoordinatesVbo);
		glDeleteBuffers(spriteIndicesVbo);
	}

}
