package com.stylelogic.predictionengine;

import java.sql.Connection;
import java.sql.SQLException;

class ProcessingDaemon extends Thread {
    private final GroupingEngine groupingEngine;
    boolean active = true;
    Connection processingConn;
    GUI gui;

    public ProcessingDaemon(GroupingEngine groupingEngine, GUI _gui) {
        this.groupingEngine = groupingEngine;
        gui = _gui;
        System.out.println("Starting Processing Daemon ");
        try {
            processingConn = groupingEngine.jdbi.createConnection();
        } catch (SQLException ex) {
            System.err.println("TaskListener SQLException: " + ex.getMessage());
            active = false;
        }
    }

    public void setActiveState(boolean _active) {
        active = _active;
    }

    public void run() {
        try {
            do {
                if (groupingEngine.taskList.size() > 0) ProcessTaskList();
                try {
                    sleep(1000 * groupingEngine.processingDaemonTimeout);
                } catch (InterruptedException ex) {
                    System.err.println("ProcessingDaemon Awakened");
                }
            } while (active);
            System.out.println("Shutting Down Processing Daemon");
        } catch (Exception ex) {
            System.err.println("ProcessingDaemon Exception: " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    public void ProcessTaskList() {
        int id, index;

        while ((groupingEngine.taskList.size()) > 0) {
            id = ((Integer) groupingEngine.taskList.removeFirst()).intValue();
            if (!groupingEngine.mine.validUserID(id)) {
                index = groupingEngine.mine.addUser(id);
//					userIndex.addID(id);
//					index = mine.getUserIndex(id);
                System.out.println("Adding new User " + id + " (" + index + "/" + groupingEngine.mine.getUserIndexSize() + ")");
//					mine.addUser(index,id);
            } else
                index = groupingEngine.mine.getUserIndex(id);
            try {
                System.out.print("Processing user " + id + " (g=" + groupingEngine.mine.users[index].groupIndex + ")");
                if (groupingEngine.kMean.isolateUser(index))                                //Must Remove User from Old Group first
                {
                    groupingEngine.dao.loadIndividualRatings(processingConn, index);                //Load User's new ratings, which have changed
                    System.out.print(" -- ");
                    if (groupingEngine.mine.users[index].ratings.length >= GroupingEngine.MIN_RATING_COUNT) {
                        groupingEngine.kMean.inspectUser(index);                                    //Locate the best group the user with the new ratings
                        groupingEngine.dao.storeUserMembership(processingConn, index);
                    }
                    System.out.println("Completed (newG=" + groupingEngine.mine.users[index].groupIndex + ")");
                } else System.err.println("- ProcessTaskList failed to isolate user " + id);
            } catch (Exception ex) {
                System.err.println("ProcessTaskList Exception on user " + id + "::" + ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }
    }

}
