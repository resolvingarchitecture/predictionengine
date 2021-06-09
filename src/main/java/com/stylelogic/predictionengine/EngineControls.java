package com.stylelogic.predictionengine;

public interface EngineControls
{
	void showGroupErrorStats();
	void showGroupDistanceStats();
	boolean haltProcessing();
	void setGroupingIterations(int n);
	void startGroupingEngine();
	void setGroupingIntermission(int seconds);
}