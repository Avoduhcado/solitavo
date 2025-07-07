package com.avogine.solitavo.scene.command;

/**
 *
 */
public interface CardOperation {

	/**
	 * 
	 */
	public void execute(float delta);
	
	public void commit();
	
	/**
	 * 
	 */
	public void rollback();
	
	/**
	 * @return
	 */
	public boolean isExecuting();
	
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
