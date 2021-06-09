package com.stylelogic.predictionengine;

class GroupingDaemon extends Thread {
    private final GroupingEngine groupingEngine;
    boolean active = true;
    GUI gui;

    public GroupingDaemon(GroupingEngine groupingEngine, GUI _gui) {
        this.groupingEngine = groupingEngine;
        gui = _gui;
        System.out.println("Starting Grouping Daemon Daemon");
    }

    public void setActiveState(boolean _active) {
        active = _active;
    }

    public void run() {

        try {
            do {
//				gui.addMessage("Forced Garbage Clean Up");
//				System.gc();
                if (groupingEngine.getProcessRequests() == 0) {
                    SiftUsers(0.05);
                    gui.addMessage("Idle Process Requests.  Sifting Users through filter");
                } else
                    gui.addMessage(groupingEngine.getProcessRequests() + " Process Requests. No Sifting.");

                gui.addMessage("Scanning Users and Groups");
                ScanUsers();
                gui.addMessage("Done Scanning Users and Groups");
                groupingEngine.storeUsers();
                groupingEngine.storeGroups();
                try {
                    sleep(1000 * groupingEngine.groupingDaemonTimeout);
                } catch (InterruptedException ex) {
                    System.err.println("GroupingDaemon Awakened");
                }
            } while (active);
            System.out.println("Shutting Down Grouping Daemon");
        } catch (Exception ex) {
            System.err.println("GroupingDaemon Exception: " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    private void SiftUsers(double filter) {
        int brk = 30, dx, dg;
        float dhdt = -1, dh, vdhdt = 0;

        for (int i = 0; i < groupingEngine.mine.getUserCount() && active; i++)
            if (Math.random() < filter)
                groupingEngine.kMean.isolateUser(i);
    }


    private void ScanUsers() {
        int brk, dx, dg;
        float dhdt = -1, dh, vdhdt = 0;

        brk = groupingEngine.grouping_iterations;
        gui.addMessage("Reactivating Group Processing (" + brk + " Rounds)");
        try {
            System.out.println("Scanning Users(" + groupingEngine.mine.getUserCount() + ") Timeout in " + brk);
            while (dhdt != 0 && active && brk > 0) {
                dx = 0;
                dh = 0;
                dhdt = 0;
                vdhdt = 0;
                for (int i = 0; i < groupingEngine.mine.getUserCount() && active; i++)
                    if (groupingEngine.mine.users[i] != null)
                        if (groupingEngine.mine.users[i].ratings.length >= GroupingEngine.MIN_RATING_COUNT) {
                            //				System.out.println("Inspecting User-"+i);
                            dh = groupingEngine.kMean.inspectUser(i);
                            if (dh != 0) {
                                dhdt += Math.abs(dh);
                                vdhdt += dh;
                                dx++;
                            }
                        }
                System.out.println("Final: DH/DT=" + vdhdt + " |dh/dt|=" + dhdt + " (" + (brk--) + ") dx/dt=" + dx + " -----------");
            }
            for (int i = 0; i < groupingEngine.mine.getGroupCount() && active; i++)
                groupingEngine.mine.groups[i].refresh();
        } catch (Exception ex) {
            System.err.println("ScanUsers Exception: " + ex.getMessage());
            ex.printStackTrace(System.out);
        }

    }

}
