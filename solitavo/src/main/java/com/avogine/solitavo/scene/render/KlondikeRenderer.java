package com.avogine.solitavo.scene.render;

import java.util.*;

import org.lwjgl.opengl.*;

import com.avogine.entity.Entity;
import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.game.util.*;
import com.avogine.render.data.experimental.*;
import com.avogine.render.shader.NormalShader;

/**
 *
 */
public class KlondikeRenderer implements Renderable, Cleanupable {

	private TableShader tableShader;
	private CardShader cardShader;
	private NormalShader normalShader;
	
	@Override
	public void onRegister(Game game) {
		tableShader = new TableShader();
		cardShader = new CardShader();
		normalShader = new NormalShader();
	}

	@Override
	public void onRender(SceneState sceneState) {
		renderTable();
		
		renderCards(sceneState.scene());
		
		renderDebugNormals(sceneState.scene());
	}
	
	private void renderTable() {
		
	}
	
	private void renderCards(Scene scene) {
		
	}
	
	private void renderDebugNormals(Scene scene) {
		normalShader.bind();
		
		normalShader.projection.loadMatrix(scene.getProjection());
		normalShader.view.loadMatrix(scene.getView());
		
		Collection<AModel> models = scene.getModelMap().values();
		for (AModel model : models) {
			List<Entity> entities = model.getEntities();
			
			for (AMaterial material : model.getMaterials()) {
				for (AMesh mesh : material.getMeshes()) {
					GL30.glBindVertexArray(mesh.getVao());
					entities.stream()
					.forEach(entity -> {
						normalShader.model.loadMatrix(entity.getTransform().modelMatrix());
						GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVerticesLength(), GL11.GL_UNSIGNED_INT, 0);
					});
				}
			}
		}
		
		GL30.glBindVertexArray(0);
		normalShader.unbind();
	}

	@Override
	public void onCleanup() {
		tableShader.cleanup();
		cardShader.cleanup();
	}
	
}
