package com.avogine.solitavo.scene.wild.command;

/**
 *
 */
public interface CardOperation {

	/**
	 * 
	 */
	public void execute();
	
	/**
	 * 
	 */
	public void rollback();
	
	/**
	 * 
	 */
	public default void describe() {
		
	}
	
}
