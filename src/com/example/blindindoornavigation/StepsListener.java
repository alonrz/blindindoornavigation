package com.example.blindindoornavigation;

/**
 * This interface contains calls related to step sensor
 */
public interface StepsListener {
	/**
	 * This method will be called every time a step has been taken and give number 
	 * of steps taken since start or last reset request. 
	 * @param steps - the total amount of steps so far
	 */
	public void stepHasBeenTaken(int steps);
	
	/**
	 * This method will be called every time a step has been taken 
	 * and provide the distance traveled since start or since last reset requested.
	 * Note the boolean value which tells if the distance is after calibration.
	 * @param hasAccurateDistance - if false - distance is estimated. if true means calibration was performed. 
	 * @param totalDistance - number of steps * step length = distance traveled since reset or start. 
	 */
	public void stepHasBeenTaken(boolean hasAccurateDistance, int totalDistance);
}
