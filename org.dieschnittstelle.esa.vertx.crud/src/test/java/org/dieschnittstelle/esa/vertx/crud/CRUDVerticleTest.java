package org.dieschnittstelle.esa.vertx.crud;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
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
        vertx.deployVerticle(CRUDVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @Test
    public void test() {

        // create request
        StationaryTouchpoint tp = new StationaryTouchpoint(-1,"dorem",new Address("lipsum","olor","-42","adispiscing"));
        CRUDRequest<StationaryTouchpoint> request = new CRUDRequest<StationaryTouchpoint>(CRUDRequest.Operation.CREATE,tp);

        logger.info("sending CRUDRequest...");

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
                StationaryTouchpoint created = result.result().body().getEntity();
                logger.info("got created: " + created);
                assertEquals(created.getName(),tp.getName());

                // create a read request
                CRUDRequest<StationaryTouchpoint> readRequest = new CRUDRequest<StationaryTouchpoint>(CRUDRequest.Operation.READ,StationaryTouchpoint.class,created.getId());

                vertx.eventBus().send(CRUDRequest.class.getName(), readRequest, new AsyncResultHandler<Message<CRUDResult<StationaryTouchpoint>>>() {
                    @Override
                    public void handle(AsyncResult<Message<CRUDResult<StationaryTouchpoint>>> result) {
                        StationaryTouchpoint read = result.result().body().getEntity();
                        logger.info("got read: " + read);
                        assertEquals(read.getName(),tp.getName());
                    }
                });

            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
