package com.stylelogic.predictionengine;

import java.util.List;
import java.util.logging.Logger;

class IdIndex {

	private static final Logger LOG = Logger.getLogger(IdIndex.class.getName());

	private	int[] ids;
	private	int[] index;
	private int indexCount;
	private final int start;
	private final double growthFactor;

	public IdIndex(List<Integer> idList, double padding) {
		int	maxID=0;
		int	minID=32667;
		int i;

		growthFactor = 1 + padding;
		ids = new int[(int)(idList.size()*growthFactor)];

		for (i=0; i<idList.size(); i++) {
			ids[i] = idList.get(i);
			if (ids[i] > maxID) maxID = ids[i]; // Determine max ID in list
			if (ids[i] < minID) minID = ids[i]; // Determine min ID in list
		}
		start = minID; // Let's start with the minimum ID
		index = new int[(int)((maxID-start + 1)*growthFactor)];
		for (i=0; i<idList.size(); i++)
			index[ idList.get(i)-start ] = i;
		indexCount = idList.size();

		LOG.info(getIndex(idList.get(1))+" = 1??");
		LOG.info(getID(1)+" = "+idList.get(1)+"??");
	}

	public boolean validID(int newID) {
		if (newID-start >= index.length)
			return false;
		return ids[index[newID - start]] == newID;
	}


	public int addID(int newID) {
		while (newID-start >= index.length)
			expandIndex();
		if (indexCount >= ids.length) {
			LOG.info(indexCount+" >= "+ ids.length+" ("+newID+")");
			expandID();
		}
		index[newID-start] = indexCount;
		ids[indexCount] = newID;
		return indexCount++;
	}

	private void expandID() {
		LOG.info("Expanding ID array from "+ ids.length+" to "+(int)(ids.length*growthFactor));
		int[] newID = new int[(int)(ids.length*growthFactor)];
		System.arraycopy(ids, 0, newID, 0, ids.length);
		ids = newID;
	}

	private void expandIndex() {
		LOG.info("Expanding Index array from"+ index.length+" to "+(int)(index.length*growthFactor));
		int[] newIndex = new int[(int)(index.length*growthFactor)];
		System.arraycopy(index, 0, newIndex, 0, index.length);
		index = newIndex;
	}

	public int getIndex(int id) {
		if (id-start >= this.index.length)
			addID(id);
		return this.index[id-start];
	}

	public int getID(int index) {
		return ids[index];
	}

	public int size() {
		return ids.length;
	}

	public int length() {
		return indexCount;
	}
}
