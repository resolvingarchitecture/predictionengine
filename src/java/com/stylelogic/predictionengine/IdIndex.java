package com.stylelogic.predictionengine;

class IdIndex {

	private	int[] id;
	private	int[] index;
	private int start;
	private int indexCount;
	private double growthFactor=1.25;

	public IdIndex() {}

	public IdIndex(int[] list, double padding)
	{
		int	maxID=0;
		int	minID=32667;
		int i=0;

		indexCount=0;
		growthFactor = 1 + padding;
		id = new int[(int)(list.length*growthFactor)];

		for (i=0; i<list.length; i++)
		{
			id[i] = list[i];
			if (list[i] > maxID) maxID = list[i];
			if (list[i] < minID) minID = list[i];
		}
		start = minID;
		index = new int[(int)((maxID-start + 1)*growthFactor)];
		for (i=0; i<list.length; i++)
			index[ list[i]-start ] = i;
		indexCount = list.length;

//		System.out.println(getIndex(list[1])+" = 1??");
//		System.out.println(getID((int)1)+" = "+list[1]+"??");

	}

	public boolean validID(int newID)
	{
		if (newID-start >= index.length) return false;
		if (id[index[newID-start]] != newID) return false;
		return true;
	}


	public int addID(int newID)
	{
		while (newID-start >= index.length)
			expandIndex();
		if (indexCount >= id.length)
		{
			System.out.println(indexCount+" >= "+ id.length+" ("+newID+")");
			expandID();
		}

		index[newID-start] = indexCount;
		id[ indexCount ] = newID;

		return (indexCount++);
	}

	private void expandID()
	{
		int[]	newID;

		System.out.println("Expanding ID array from "+ id.length+" to "+(int)(id.length*growthFactor));
		newID = new int[(int)(id.length*growthFactor)];
		for (int i = 0; i< id.length; i++)
			newID[i] = id[i];
		id = newID;
	}
	private void expandIndex()
	{
		int[]	newIndex;

		System.out.println("Expanding Index array from"+ index.length+" to "+(int)(index.length*growthFactor));
		newIndex = new int[(int)(index.length*growthFactor)];
		for (int i = 0; i< index.length; i++)
			newIndex[i] = index[i];
		index = newIndex;
	}

	public int getIndex( int id )
	{
		int index=-1;

		try{
			if (id-start >= this.index.length)
				addID(id);
			index = this.index[id-start];
		}
		catch(Exception ex){ System.err.println("getIndex Exception (" +id+") :"+ ex.getMessage());ex.printStackTrace(System.out);}

		return (index);
	}

	public int getID( int index )
	{
		return (id[index]);
	}

	public int size()
	{
		return (id.length);
	}

	public int length()
	{
		return (indexCount);
	}
}
