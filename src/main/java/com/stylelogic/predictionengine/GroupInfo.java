package com.stylelogic.predictionengine;

import java.util.Arrays;
import java.util.logging.Logger;

public class GroupInfo {

	private static final Logger LOG = Logger.getLogger(GroupInfo.class.getName());

	double groupHappiness;
	double userGrowthFactor;
	double itemGrowthFactor;
	double memberItemRatingSD;
	double itemRatingSD;
	double itemRatingMean;
	double predictiveError;
	int itemRatingN;
	int	memberCount;
	int groupIndex;
	int valueFargID;
	int histFargID;
	int dx;
	int ratingCount=0;
	GroupRatings[] ratings;
	byte[] userList;
	boolean	modified;

	final static short DIRTY_DX = 10;

	public GroupInfo(int groupIndex, int itemCount, int userCount, double itemGrowthFactor, double userGrowthFactor) {
		dx=0;
		valueFargID = -1;
		histFargID = -1;
		this.userGrowthFactor = 1.0+userGrowthFactor;
		this.itemGrowthFactor = 1.0+itemGrowthFactor;
		valueFargID = 82+groupIndex;
		histFargID = 132+groupIndex;
		this.groupIndex =groupIndex;
		memberCount=0;
		userList = new byte[(int)((userCount/8+1)* this.userGrowthFactor)];
		ratings = new GroupRatings[(int)(itemCount* this.itemGrowthFactor)];
		Arrays.fill(userList, (byte) 0);
		for (int i=0;i<itemCount;i++)
			ratings[i] = new GroupRatings(0, (short) 0);
		modified = false;

		ratingCount = itemCount;
		groupHappiness = 0;
		memberItemRatingSD = 0;
		itemRatingSD = 0;
		itemRatingMean =0;
		predictiveError = 0;
		itemRatingN = 0;
	}

	public void refresh() {
		int votes;
		long avg;

		if (dx > DIRTY_DX) {
			LOG.info("Cleaning Data for Group "+groupIndex);
			dx=0;
			for (int i=0; i <ratingCount; i++) {
				avg=0;
				votes=0;
				for (int j=1; j<ratings[i].ratings.length; j++) {
					avg = avg + j*ratings[i].ratings[j];
					votes += ratings[i].ratings[j];
				}
				if (votes > 0)
					ratings[i].rating = (avg << 32) / votes;
			}
		}
	}

//	public boolean validateItemReview(int i) {
//		float avg;
//		short votes;
//		boolean valid;
//
//		avg = (float) 0.0;
//		votes = 0;
//		for (int j=1; j<ratings[i].ratings.length; j++)
//		{
//			avg = avg + j*ratings[i].ratings[j];
//			votes += ratings[i].ratings[j];
//		}
//		if (votes > 0)
//			avg /= votes;
//		valid = (Math.abs( (float) ratings[i].rating/ ((long) 1 << 32)-avg) < 0.001);
//		if (!valid)
//			LOG.info((float) ratings[i].rating/ ((long) 1 << 32)+" V "+avg+" ");
//		ratings[i].valid = valid;
//		return valid;
//	}


	public void addUser(int userIndex, ItemRatings[] userRatings, float userHappiness)
	{
		int	itemIndex;
//		boolean	valid;
		dx++;
		for (int i=0; i <userRatings.length; i++)
			if (userRatings[i].rating > 0) {
				itemIndex = userRatings[i].itemIndex;
				ratings[itemIndex].modified = true;
	//			System.out.println("group "+groupIndex+", item "+itemIndex+", weight "+ratings[itemIndex].weight);
				ratings[itemIndex].rating = (long) (ratings[itemIndex].rating*ratings[itemIndex].weight+((long)userRatings[i].rating<<32))/(ratings[itemIndex].weight+1);
				ratings[itemIndex].weight++;
				ratings[itemIndex].ratings[ userRatings[i].rating ]++;

/*
				if (ratings[i].valid)
					if (!validateItemReview(itemIndex))
					{
						System.out.print("User "+userIndex+"("+userRatings[i].rating+")-->"+groupIndex+"(dx="+dx+") failed; Item "+itemIndex+"::"+( (float)ratings[itemIndex].rating/((long) 1<<32) )+" ("+ratings[itemIndex].weight+")");
						for (int k=1;k<7;k++)
							System.out.print(ratings[itemIndex].ratings[k]+" ");
						System.out.println("");
					}
*/
			}

//		groupHappiness += userHappiness;
		while (userIndex/8 >= userList.length)
			expandUsers();
		userList[userIndex/8] ^= 1<<userIndex%8;
		memberCount++;
		modified = true;
	}

	public boolean removeUser(int userIndex, ItemRatings[] userRatings, float userHappiness) {
		int	itemIndex=0;
		int i=0;

		if (memberCount > 1) {
			dx++;
			try{
				modified = true;
				for (i=0; i <userRatings.length; i++)
					if (userRatings[i].rating > 0) {
						itemIndex = userRatings[i].itemIndex;
						ratings[itemIndex].modified = true;
						if (ratings[itemIndex].weight > 1) {
							ratings[itemIndex].rating = (long) (ratings[itemIndex].rating*ratings[itemIndex].weight-((long)userRatings[i].rating<<32))/(ratings[itemIndex].weight-1);
							ratings[itemIndex].weight--;
						} else {
							ratings[itemIndex].rating=0;
							ratings[itemIndex].weight=0;
						}
						if (ratings[itemIndex].ratings[ userRatings[i].rating ] > 0)
							ratings[itemIndex].ratings[ userRatings[i].rating ]--;

/*
						if (ratings[i].valid)
							if (!validateItemReview(itemIndex))
							{
								System.out.print("User "+userIndex+"("+userRatings[i].rating+")<==="+groupIndex+"(dx="+dx+") failed; Item "+itemIndex+"::"+( (float)ratings[itemIndex].rating/((long) 1<<32) )+" ("+ratings[itemIndex].weight+")");
								for (int k=1;k<7;k++)
									System.out.print(ratings[itemIndex].ratings[k]+" ");
								System.out.println("");
							}
*/

					}
//				groupHappiness -= userHappiness;
				userList[userIndex/8] ^= 1<<userIndex%8;
				memberCount--;
			} catch(Exception ex){
				LOG.info(itemIndex+" rating "+userRatings[i].rating+" weight="+ratings[itemIndex].weight);
				LOG.warning("removeUser Exception: " + ex.getMessage());
			}
			return true;
		} else
			return false;
	}

//	public long getRating(int itemIndex) {
//		return ((itemIndex<ratingCount)?ratings[itemIndex].rating:-1);
//	}

	public void addItem(int id) {
		while (id >= ratings.length)
			expandItems();
		ratings[id] = new GroupRatings(0,(short)0);
		ratingCount++;
	}

	private void expandUsers() {
		byte[] newUserList = new byte[(int)(userList.length* userGrowthFactor)];
		LOG.info("Expanding userList array from "+userList.length+" to "+(int)(userList.length* userGrowthFactor));
		System.arraycopy(userList, 0, newUserList, 0, userList.length);
		for (int i=userList.length; i<newUserList.length; i++)
			newUserList[i] = 0;
		userList = newUserList;
	}

	private void expandItems() {
		GroupRatings[] newRatings = new GroupRatings[(int)(ratings.length* itemGrowthFactor)];
		System.arraycopy(ratings, 0, newRatings, 0, ratings.length);
		for (int i=ratings.length; i<newRatings.length; i++)
			newRatings[i] = new GroupRatings(0,(short) 0);
		ratings = newRatings;
	}
}


