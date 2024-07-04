package com.avogine.solitavo.scene.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.Math;

import org.joml.*;
import org.joml.primitives.Rectanglef;
import org.lwjgl.opengl.GL11;

import com.avogine.game.Game;
import com.avogine.game.util.Cleanupable;

/**
 *
 */
public class DebugRenderer implements Cleanupable {

	private DebugShader debugShader;
	
	private int spriteVao;
	
	@Override
	public void onRegister(Game game) {
		init(game.getCurrentScene().getProjection());
	}
	
	private void init(Matrix4f projection) {
		debugShader = new DebugShader();
		debugShader.bind();
		debugShader.projection.loadMatrix(projection);
		debugShader.unbind();
		
		var rectangleVertices = new float[] {
				// pos	
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f, 
				0.0f, 0.0f,
		};

		spriteVao = glGenVertexArrays();
		
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, rectangleVertices, GL_STATIC_DRAW);
		
		glBindVertexArray(spriteVao);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	/**
	 * @param rectangleX 
	 * @param rectangleY 
	 * @param rectangleWidth 
	 * @param rectangleHeight 
	 * @param rotation
	 * @param scale Apply scaling from center of sprite.
	 */
	public void renderRect(float rectangleX, float rectangleY, float rectangleWidth, float rectangleHeight, float rotation, float scale, Vector4f color) {
		glLineWidth(2.0f);
		glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		debugShader.bind();
		
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(rectangleX, rectangleY, 0);
		
		modelMatrix.translate(rectangleWidth * 0.5f, rectangleHeight * 0.5f, 0);
		modelMatrix.rotateZ((float) Math.toRadians(rotation));
		modelMatrix.translate(-rectangleWidth * 0.5f, -rectangleHeight * 0.5f, 0);
		
		modelMatrix.scale(rectangleWidth, rectangleHeight, 1f);
		
		modelMatrix.scaleAroundLocal(scale, rectangleX + (rectangleWidth * 0.5f), rectangleY + (rectangleHeight * 0.5f), 1f);
		debugShader.model.loadMatrix(modelMatrix);
		
		debugShader.lineColor.loadVec4(color);
		
		glBindVertexArray(spriteVao);
		glDrawArrays(GL_LINE_LOOP, 0, 4);
		glBindVertexArray(0);
		
		debugShader.unbind();
		
		glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		glLineWidth(1.0f);
	}
	
	/**
	 * @param position
	 * @param size
	 */
	public void renderRect(float rectangleX, float rectangleY, float rectangleWidth, float rectangleHeight, Vector4f color) {
		renderRect(rectangleX, rectangleY, rectangleWidth, rectangleHeight, 0, 1, color);
	}
	
	/**
	 * @param rectangle
	 */
	public void renderRect(Rectanglef rectangle, Vector4f color) {
		renderRect(rectangle.minX, rectangle.minY, rectangle.lengthX(), rectangle.lengthY(), color);
	}

	@Override
	public void onCleanup() {
		if (debugShader != null) {
			debugShader.cleanup();
		}
		glDeleteVertexArrays(spriteVao);
	}
	
}
