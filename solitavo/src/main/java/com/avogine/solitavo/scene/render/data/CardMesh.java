package com.avogine.solitavo.scene.render.data;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

/**
 *
 */
public class CardMesh {

	private int vao;
	
	private int vbo;
	private int ebo;
	
	/**
	 * @param vertices 
	 * @param indices 
	 */
	public CardMesh(float[] vertices, int[] indices) {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
		FloatBuffer vertexBuffer = MemoryUtil.memCallocFloat(vertices.length);
		vertexBuffer.put(0, vertices);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		
		IntBuffer indexBuffer = MemoryUtil.memCallocInt(indices.length);
		indexBuffer.put(0, indices);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
		
		glBindVertexArray(0);
	}
	
	/**
	 * @return the vao
	 */
	public int getVao() {
		return vao;
	}
	
	public void cleanup() {
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		glDeleteVertexArrays(vao);
	}
	
}
