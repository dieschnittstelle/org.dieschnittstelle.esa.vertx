package org.dieschnittstelle.esa.vertx.crud.jpa;

import java.util.List;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.api.POJOMessageCodec;
import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;


/**
 * Created by master on 27.05.16.
 */
@RunWith(VertxUnitRunner.class)
public class CRUDVerticleTest {

    protected static Logger logger = Logger.getLogger(CRUDVerticleTest.class);

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        vertx.eventBus().registerDefaultCodec(CRUDRequest.class,new POJOMessageCodec(CRUDRequest.class));
        vertx.eventBus().registerDefaultCodec(CRUDResult.class,new POJOMessageCodec(CRUDResult.class));

        // deploy the verticle as a worker?
        vertx.deployVerticle(CRUDVerticleHibernate.class.getName(),new DeploymentOptions().setWorker(true),
                context.asyncAssertSuccess());
    }

    @Test
    public void test() {

//        // readAll
//        CRUDRequest<StationaryTouchpoint> request = new CRUDRequest<StationaryTouchpoint>(CRUDRequest.Operation.READALL,StationaryTouchpoint.class);
//        logger.info("sending readall CRUDRequest...");
//        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {
//
//            @Override
//            public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
//                List<StationaryTouchpoint> tps = result.result().body().getEntityList();
//                logger.info("got original touchpoints list: " + tps);
//                assertNotNull(tps);
//
//                // remember the size for checking later whether the size will be incremented on create
//                int initialSize = tps.size();
//
//                // create request
//                StationaryTouchpoint tp = new StationaryTouchpoint(-1, "dorem", new Address("lipsum", "olor", "-42", "adispiscing"));
//                CRUDRequest<StationaryTouchpoint> request = new CRUDRequest<StationaryTouchpoint>(CRUDRequest.Operation.CREATE, tp);
//
//                logger.info("sending create CRUDRequest...");
//
//                vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {
//                    @Override
//                    public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
//                        StationaryTouchpoint created = result.result().body().getEntity();
//                        logger.info("got created: " + created);
//                        assertEquals(created.getName(), tp.getName());
//
//                        // create a read request
//                        CRUDRequest<StationaryTouchpoint> readRequest = new CRUDRequest<StationaryTouchpoint>(CRUDRequest.Operation.READ, StationaryTouchpoint.class, created.getId());
//
//                        vertx.eventBus().send(CRUDRequest.class.getName(), readRequest, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {
//                            @Override
//                            public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
//                                StationaryTouchpoint read = result.result().body().getEntity();
//                                logger.info("got read: " + read);
//                                assertEquals(read.getName(), tp.getName());
//
//                                // readAll again
//                                CRUDRequest<StationaryTouchpoint> request = new CRUDRequest<StationaryTouchpoint>(CRUDRequest.Operation.READALL,StationaryTouchpoint.class);
//                                logger.info("sending readall CRUDRequest...");
//                                vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {
//
//                                    @Override
//                                    public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
//                                        List<StationaryTouchpoint> tps = result.result().body().getEntityList();
//                                        logger.info("got new touchpoints list: " + tps);
//                                        assertNotNull(tps);
//                                        assertEquals(tps.size(),initialSize+1);
//                                    }
//                                });
//
//                            }
//                        });
//
//                    }
//                });
//            }
//        });
//
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }


}
