package com.avogine.solitavo.scene.klondike.command;

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
	
}
