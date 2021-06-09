package com.stylelogic.predictionengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class TaskListener extends Thread {
    private final GroupingEngine groupingEngine;
    boolean active = true;
    Connection taskListenerConn;
    PreparedStatement ps;
    GUI gui;

    public TaskListener(GroupingEngine groupingEngine, GUI _gui) {
        this.groupingEngine = groupingEngine;
        System.out.println("Starting Listener Thread");
        gui = _gui;
        try {
            taskListenerConn = groupingEngine.jdbi.createConnection();
            ps = groupingEngine.jdbi.createPreparedStatement(taskListenerConn, "exec usp_DM_User_Process_Request");
        } catch (SQLException ex) {
            System.err.println("TaskListener SQLException: " + ex.getMessage());
            active = false;
        }
    }

    public void setActiveState(boolean _active) {
        active = _active;
    }

    public void run() {
        while (active) {
            BuildTaskList();
            try {
                sleep(1000 * groupingEngine.taskListenerTimeout);
            } catch (InterruptedException ex) {
                System.err.println("TaskListener Awakened");
            }
        }
        System.out.println("Shutting Down Listener Thread");
    }

    private int BuildTaskList() {
        int count = 0;
        int userID, uIndex;
        String s;
        try {

//			System.out.print("Reading Task List----------------");
            ResultSet rs = groupingEngine.jdbi.executePreparedStatement(ps);
            s = "Request to Process :";
            while (rs.next()) {
                count++;
                userID = rs.getInt("userID");
                s += userID + ";";
                groupingEngine.taskList.add((Object) new Integer(userID));
            }
            if (count > 0) {
                System.out.println(s);
                gui.addMessage(s);
            }
            rs.close();

        } catch (Exception ex) {
            System.err.println("ReadTaskList SQLException: " + ex.getMessage());
            return 0;
        }
        if (count > 0) groupingEngine.processingDaemon.interrupt();
        groupingEngine.incrementProcessRequests(count);
        return (count);
    }

}
