package com.avogine.solitavo.scene;

import org.joml.Matrix4f;

import com.avogine.game.scene.Projection;

/**
 * Orthographic {@link Projection} implementation.
 */
public class OrthoProjection extends Projection {

	/**
	 * @param width
	 * @param height
	 */
	public OrthoProjection(int width, int height) {
		super(width, height);
	}
	
	@Override
	public Matrix4f invert() {
		return getProjectionMatrix().invertOrtho(getInvertedProjectionMatrix());
	}
	
	@Override
	public void setAspectRatio(int width, int height) {
		getProjectionMatrix().setOrtho2D(0, width, height, 0);
	}

}
