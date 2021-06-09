package com.stylelogic.predictionengine;

public interface GroupingAlgorithm
{
	void init(int kValue);
	void start();
	boolean isolateUser(int userIndex);
	String showGroupErrorStats();
	String showGroupDistanceStats();
	float compileHappiness();
}