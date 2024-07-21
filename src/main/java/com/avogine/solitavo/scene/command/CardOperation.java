package com.avogine.solitavo.scene.command;

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
	 * @return True if this operation should increment the game's total moves counter.
	 */
	public default boolean incrementsMoves() {
		return false;
	}
	
	/**
	 * 
	 */
	public default void describe() {
		
	}
	
}
