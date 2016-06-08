package org.dieschnittstelle.esa.vertx.main;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.api.POJOMessageCodec;
import org.dieschnittstelle.esa.vertx.crud.mongod.CRUDVerticleMongod;
import org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.esa.vertx.crud.jpa.CRUDVerticleHibernate;
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

        vertx.eventBus().registerDefaultCodec(CRUDRequest.class,new POJOMessageCodec(CRUDRequest.class));
        vertx.eventBus().registerDefaultCodec(CRUDResult.class,new POJOMessageCodec(CRUDResult.class));

        CRUDServiceVerticle serviceVerticle = new CRUDServiceVerticle();
        serviceVerticle.addClassMapping("touchpoints", StationaryTouchpointDoc.class);

        vertx.deployVerticle(serviceVerticle, stringAsyncResult -> {
            logger.info("start(): deployed CRUDServiceVerticle");
            vertx.deployVerticle(CRUDVerticleHibernate.class.getName(), stringAsyncResult1 -> {
                logger.info("start(): deployed CRUDVerticleHibernate");
                vertx.deployVerticle(CRUDVerticleMongod.class.getName(), stringAsyncResult2 -> {
                    logger.info("start(): deployed CRUDVerticleMongod");
                    logger.info("start(): done.");
                    fut.complete();
                });
            });
        });
    }



}
