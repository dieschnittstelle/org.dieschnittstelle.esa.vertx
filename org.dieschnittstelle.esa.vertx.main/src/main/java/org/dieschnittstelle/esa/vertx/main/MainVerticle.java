package org.dieschnittstelle.esa.vertx.main;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.esa.vertx.crud.CRUDVerticle;
import org.dieschnittstelle.esa.vertx.webapi.CRUDServiceVerticle;

import org.apache.log4j.Logger;

/**
 * Created by master on 01.06.16.
 */
public class MainVerticle extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(MainVerticle.class);

    public void start(Future<Void> fut) {

        logger.info("start()");

        vertx = Vertx.vertx();

        CRUDServiceVerticle serviceVerticle = new CRUDServiceVerticle();
        serviceVerticle.addClassMapping("touchpoints", StationaryTouchpoint.class);

        vertx.deployVerticle(serviceVerticle, stringAsyncResult -> {
            logger.info("start(): deployed CRUDServiceVerticle");
            vertx.deployVerticle(CRUDVerticle.class.getName(), stringAsyncResult1 -> {
                logger.info("start(): deployed CRUDVerticle");
                logger.info("start(): done.");

                fut.complete();
            });
        });
    }



}
