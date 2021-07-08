package com.stylelogic.predictionengine;

import ra.common.Envelope;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;

import java.util.Properties;
import java.util.logging.Logger;

public class PredictionEngineService extends BaseService {

    private static final Logger LOG = Logger.getLogger(PredictionEngineService.class.getName());

    private static final GroupingEngine engine = new GroupingEngine();

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {

        }
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting...");
        updateStatus(ServiceStatus.STARTING);
        LOG.info(" Prediction Engine initializing... ");
        if(!engine.init()) {
            LOG.severe("Initialization failed.");
            return false;
        }
        engine.start();
        return true;
    }

    @Override
    public boolean shutdown() {
        engine.shutDown();
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        engine.gracefulShutdown();
        return false;
    }
}
