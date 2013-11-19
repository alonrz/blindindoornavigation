package com.example.blindindoornavigation;

/**
 * This interface contains calls related to step sensor
 */
public interface StepsListener {
	/**
	 * This method will be called every time a step has been taken. 
	 * @param steps - the total amount of steps so far
	 */
	public void stepHasBeenTaken(int steps);
	
}
