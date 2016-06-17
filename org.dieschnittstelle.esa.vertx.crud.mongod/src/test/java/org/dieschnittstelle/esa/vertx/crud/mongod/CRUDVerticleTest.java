package org.dieschnittstelle.esa.vertx.crud.mongod;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.api.POJOMessageCodec;
import org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc;
import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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

        vertx.deployVerticle(CRUDVerticleMongod.class.getName(),
                context.asyncAssertSuccess());
    }

    @Test
    public void test() {
        // first of all reset
        CRUDRequest<StationaryTouchpointDoc> prepare = new CRUDRequest<StationaryTouchpointDoc>(CRUDRequest.Operation.DELETEALL,StationaryTouchpointDoc.class);
        vertx.eventBus().send(CRUDRequest.class.getName(), prepare, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {

            @Override
            public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
                logger.info("prepared test, deleted collection...");

                // readAll
                CRUDRequest<StationaryTouchpointDoc> request = new CRUDRequest<StationaryTouchpointDoc>(CRUDRequest.Operation.READALL, StationaryTouchpointDoc.class);
                logger.info("sending readall CRUDRequest...");
                vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {

                    @Override
                    public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
                        List<StationaryTouchpoint> tps = result.result().body().getEntityList();
                        logger.info("got original touchpoints list: " + tps);
                        assertNotNull(tps);
                        assertEquals(tps.size(),0);

                        // remember the size for checking later whether the size will be incremented on create
                        int initialSize = tps.size();

                        // create request
                        StationaryTouchpointDoc tp = new StationaryTouchpointDoc(-1, "dorem", new Address("lipsum", "olor", "-42", "adispiscing"));
                        CRUDRequest<StationaryTouchpointDoc> request = new CRUDRequest<StationaryTouchpointDoc>(CRUDRequest.Operation.CREATE, tp);

                        logger.info("sending create CRUDRequest...");

                        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpointDoc>>>() {
                            @Override
                            public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpointDoc>>> result) {
                                StationaryTouchpointDoc created = result.result().body().getEntity();
                                logger.info("got created: " + created);
                                assertEquals(created.getName(), tp.getName());

                                // create a read request
                                CRUDRequest<StationaryTouchpointDoc> readRequest = new CRUDRequest<StationaryTouchpointDoc>(CRUDRequest.Operation.READ, StationaryTouchpointDoc.class, created.get_id());

                                vertx.eventBus().send(CRUDRequest.class.getName(), readRequest, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpointDoc>>>() {
                                    @Override
                                    public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpointDoc>>> result) {
                                        StationaryTouchpoint read = result.result().body().getEntity();
                                        logger.info("got read: " + read);
                                        assertEquals(read.getName(), tp.getName());

                                        // readAll again
                                        CRUDRequest<StationaryTouchpointDoc> request = new CRUDRequest<StationaryTouchpointDoc>(CRUDRequest.Operation.READALL, StationaryTouchpointDoc.class);
                                        logger.info("sending readall CRUDRequest...");
                                        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpointDoc>>>() {

                                            @Override
                                            public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpointDoc>>> result) {
                                                List<StationaryTouchpointDoc> tps = result.result().body().getEntityList();
                                                logger.info("got new touchpoints list: " + tps);
                                                assertNotNull(tps);
                                                assertEquals(tps.size(), initialSize + 1);
                                            }
                                        });

                                    }
                                });

                            }
                        });
                    }
                });
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
