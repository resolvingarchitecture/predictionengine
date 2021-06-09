package com.stylelogic.predictionengine;

import java.util.logging.Logger;

class LazyWriter extends Thread {

    private static final Logger LOG = Logger.getLogger(LazyWriter.class.getName());

    private final GroupingEngine groupingEngine;
    boolean active = true;
    boolean[] task;
    GUI gui;
    private final DAO dao;

    public LazyWriter(GroupingEngine groupingEngine, GUI gui, DAO dao) {
        this.groupingEngine = groupingEngine;
        this.gui = gui;
        this.dao = dao;
        task = new boolean[2];
        LOG.info("Starting Lazy Writer Daemon");
    }

    public void setActiveState(boolean active) {
        this.active = active;
    }

    public void run() {
        int i;
        try {
            do {
				LOG.info("LazyWriter looking for work.");
                for (i = 0; i < task.length; i++)
                    if (task[i]) ExecuteTask(i);
                try {
                    sleep(1000L * groupingEngine.lazyWriterTimeout);
                } catch (InterruptedException ex) {
                    System.err.println("LazyWriter Awakened");
                }
            } while (active);
            System.out.println("LazyWriter Writing Remaining Data");

            for (i = 0; i < task.length; i++) ExecuteTask(i);    //Do a final write to the database

            System.out.println("Shutting Down LazyWriter");
        } catch (Exception ex) {
            System.err.println("LazyWriter Exception: " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    private void ExecuteTask(int taskID) {
        try {
            System.out.println("LazyWriter Executing Task " + taskID);
            switch (taskID) {
                case 0: {
                    gui.addMessage("Start Writing to Database - User Memberships");
                    groupingEngine.dao.updateUserInfo();
                    gui.addMessage("Completed Writing to Database - User Memberships");
                }
                break;
                case 1: {
                    gui.addMessage("Start DTS - Group Ratings & Reviews");
                    int groupCount = groupingEngine.mine.getGroupCount();
                    for (int i = 0; i < groupCount; i++)
                        groupingEngine.kMean.refreshStats(i);
                    groupingEngine.dao.storeGroupRatings();
                    gui.addMessage("Completed DTS - Group Ratings & Reviews");
                }
                break;
            }
        } catch (Exception ex) {
            System.err.println("LazyWriter ExecuteTask Exception task (" + taskID + ") : " + ex.getMessage());
            ex.printStackTrace(System.out);
            return;
        }
        task[taskID] = false;
    }
}
