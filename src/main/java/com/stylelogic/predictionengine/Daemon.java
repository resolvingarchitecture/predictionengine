package com.stylelogic.predictionengine;

import ra.util.Config;

import java.util.logging.Logger;

public class Daemon {

    private static final Logger LOG = Logger.getLogger(Daemon.class.getName());

    private static final PredictionEngineService service = new PredictionEngineService();

    public static void main(String[] args) {
        LOG.info(" Prediction Engine initializing... ");
        if(!service.start(Config.loadFromMainArgs(args))) {
            System.exit(-1);
        }
    }

}
