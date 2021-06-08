package com.stylelogic.predictionengine;

import java.util.logging.Logger;

public class Daemon {

    private static final Logger LOG = Logger.getLogger(Daemon.class.getName());

    private static final GroupingEngine engine = new GroupingEngine();

    public static void main(String[] args) {
        LOG.info(" Prediction Engine initializing... ");
        if(!engine.init()) System.exit(1);
        engine.start();
    }

}
