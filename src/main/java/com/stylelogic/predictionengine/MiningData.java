package com.stylelogic.predictionengine;

import java.util.List;

public class MiningData {

	int	userCount=0;
	int groupCount=0;
	int itemCount=0;
	IdIndex	itemIndex;
	IdIndex userIndex;
	IdIndex groupIndex;
	UserInfo[] users;
	GroupInfo[] groups;
	private int	kValue = 80;

	final static double USER_INDEX_PADDING = 0.25;
	final static double GROUP_INDEX_PADDING = 1.00;
	final static double ITEM_INDEX_PADDING = 0.10;

	public MiningData() {}

	public void prepareUserIndex(List<Integer> list) {
		userIndex = new IdIndex(list, USER_INDEX_PADDING);
		userCount = userIndex.length();
		users = new UserInfo[userIndex.size()];
		for (int index=0;index<userCount;index++)
			users[index] = new UserInfo(index,userIndex.getID(index));
	}

	public int addUser(int id) {
		int index = userIndex.addID(id);
		if (index >= users.length ) expandUsers();
		userCount = userIndex.length();
		users[index] = new UserInfo(index,id);
		return index;
	}

	private void expandUsers() {
		UserInfo[] newUsers = new UserInfo[ (int) (users.length * (USER_INDEX_PADDING +1.0)) ];
		System.arraycopy(users, 0, newUsers, 0, users.length);
		users = newUsers;
	}

	public void prepareItemIndex(List<Integer> itemIds) {
		itemIndex = new IdIndex(itemIds, ITEM_INDEX_PADDING);
		itemCount = itemIndex.size();
	}

	public void addItem(int id) {
		itemIndex.addID(id);
		itemCount = itemIndex.length();
	}

	public void prepareGroupIndex(List<Integer> userGroupIds) {
		groupIndex = new IdIndex(userGroupIds, GROUP_INDEX_PADDING);
		groupCount = groupIndex.length();
		groups = new GroupInfo[groupIndex.size()];
		for (int index=0;index<groupCount;index++)
			groups[index] = new GroupInfo(index,itemCount,userCount, ITEM_INDEX_PADDING, USER_INDEX_PADDING);
		if (userGroupIds.size()>kValue)
			kValue=userGroupIds.size();
	}

	public int addGroup(int id) {
		int index = groupIndex.addID(id);
		groupCount = groupIndex.length();
		if (index >= groups.length ) expandGroups();
		groups[index] = new GroupInfo(index,itemCount,userCount, ITEM_INDEX_PADDING, USER_INDEX_PADDING);
		return index;
	}

	private void expandGroups() {
		GroupInfo newGroups[] = new GroupInfo[ (int) (groups.length * (GROUP_INDEX_PADDING +1.0)) ];
		for (int i=0; i<groups.length; i++) {
			newGroups[i] = groups[i];
		}
		groups = newGroups;
	}


	public int getUserIndex(int id) {
		return userIndex.getIndex(id);
	}

	public int getItemIndex(int id) {
		return itemIndex.getIndex(id);
	}

	public int getGroupIndex(int id) {
		return groupIndex.getIndex(id);
	}

	public boolean validUserID(int id) {
		return userIndex.validID(id);
	}

	public boolean validItemID(int id) {
		return itemIndex.validID(id);
	}

	public boolean validGroupID(int id) {
		return groupIndex.validID(id);
	}

	public int getUserID(int index) {
		return userIndex.getIndex(index);
	}

	public int getItemID(int index) {
		return itemIndex.getIndex(index);
	}

	public int getGroupID(int index) {
		return groupIndex.getIndex(index);
	}

	public int getUserCount() {
		return userCount;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public int getUserIndexSize() {
		return userIndex.size();
	}

	public int getItemIndexSize() {
		return itemIndex.size();
	}

	public int getGroupIndexSize() {
		return groupIndex.size();
	}

}


