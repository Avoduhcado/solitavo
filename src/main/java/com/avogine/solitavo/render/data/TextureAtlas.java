package com.avogine.solitavo.render.data;

import java.util.Objects;

import com.avogine.render.data.texture.Texture;

/**
 * A tightly packed texture atlas where each cell has the same dimensions
 * @param texture The entire texture sheet.
 * @param columns The number of columns in the texture sheet.
 * @param rows The number of rows in the texture sheet.
 * @param cellWidth The width in texels of each cell.
 * @param cellHeight The height in texels of each cell.
 */
public record TextureAtlas(Texture texture, int columns, int rows, float cellWidth, float cellHeight) {
	
	/**
	 * 
	 */
	public TextureAtlas {
		Objects.requireNonNull(texture);
		if (columns < 1) {
			throw new IllegalArgumentException("TextureAtlas must contain 1 or more columns.");
		}
		if (rows < 1) {
			throw new IllegalArgumentException("TextureAtlas must contain 1 or more rows.");
		}
	}
	
	/**
	 * @param texture
	 * @param columns
	 * @param rows
	 */
	public TextureAtlas(Texture texture, int columns, int rows) {
		this(texture, columns, rows, 1.0f / columns, 1.0f / rows);
	}
	
}
