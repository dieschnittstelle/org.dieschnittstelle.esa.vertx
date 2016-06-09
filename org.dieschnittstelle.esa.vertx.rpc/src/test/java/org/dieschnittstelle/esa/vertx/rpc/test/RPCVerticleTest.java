package org.dieschnittstelle.esa.vertx.rpc.test;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.rpc.ProxyFactory;
import org.dieschnittstelle.esa.vertx.rpc.test.verticle.TestLocal;
import org.dieschnittstelle.esa.vertx.rpc.test.verticle.TestRPVCVerticle;
import org.dieschnittstelle.esa.vertx.rpc.test.verticle.TestRemote;
import org.dieschnittstelle.jee.esa.entities.crm.AbstractTouchpoint;
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
public class RPCVerticleTest {

    protected static Logger logger = Logger.getLogger(RPCVerticleTest.class);

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        Verticle v = new TestRPVCVerticle();
        vertx.deployVerticle(v,
                context.asyncAssertSuccess());
    }

    @Test
    public void testLocal() {

        // create request
        StationaryTouchpoint tp = new StationaryTouchpoint(-1,"dorem",new Address("lipsum","olor","-42","adispiscing"));

        TestLocal proxy = ProxyFactory.getInstance().createProxy(TestLocal.class,vertx);
        Future<AbstractTouchpoint> fut = Future.future();

        fut.setHandler(objectAsyncResult -> {
            AbstractTouchpoint t = objectAsyncResult.result();
            logger.info("got result: " + t);
            // over the local interface, the sent and the received object should be identical!
            assertEquals(tp,t);
        });
        proxy.createTouchpoint(tp,fut);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

//    @Test
//    public void testRemote() {
//
//        // create request
//        StationaryTouchpoint tp = new StationaryTouchpoint(-1,"dorem",new Address("lipsum","olor","-42","adispiscing"));
//
//        TestRemote proxy = (TestRemote) ProxyFactory.getInstance().createProxy(TestLocal.class,vertx);
//        Future fut = Future.future();
//
//        fut.setHandler(objectAsyncResult -> {
//            logger.info("got result: " + objectAsyncResult);
//            assertNotNull(objectAsyncResult);
//        });
//        proxy.createTouchpoint(tp,fut);
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

}
