package com.stylelogic.predictionengine;

import java.util.logging.Logger;

public class UserInfo {

	private static final Logger LOG = Logger.getLogger(UserInfo.class.getName());

	float happiness;
	int	groupIndex;
	int userIndex;
	int userID;
	ItemRatings[] ratings;
	boolean	modified;

	public UserInfo(int userIndex, int userID) {
		this.userIndex = userIndex;
		this.userID = userID;
		setGroup(-1,0);
		ratings = new ItemRatings[0];
		modified = false;
	}
	public UserInfo(int userIndex, int userID, int[] itemIndex, byte[] ratings, int length) {
		this.userIndex = userIndex;
		this.userID = userID;
		setGroup(-1,0);
		setRatings(itemIndex, ratings, length);
		modified = false;
	}
	public void setGroup(int groupIndex, float happiness) {
		this.groupIndex = groupIndex;
		this.happiness = happiness;
		modified = true;
	}
	public void setRatings(int[] itemIndex, byte[] ratings, int length) {
		this.ratings = new ItemRatings[length];
		for (int i=0; i<length; i++)
			this.ratings[i] = new ItemRatings(itemIndex[i],ratings[i]);
	}
}

