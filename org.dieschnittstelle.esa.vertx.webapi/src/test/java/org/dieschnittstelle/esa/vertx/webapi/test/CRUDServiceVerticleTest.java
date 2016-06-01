package org.dieschnittstelle.esa.vertx.webapi.test;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.esa.vertx.crud.CRUDVerticle;
import org.dieschnittstelle.esa.vertx.webapi.CRUDServiceVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by master on 27.05.16.
 *
 * following http://vertx.io/blog/my-first-vert-x-3-application/
 */
@RunWith(VertxUnitRunner.class)
public class CRUDServiceVerticleTest {

    protected static Logger logger = Logger.getLogger(CRUDServiceVerticleTest.class);

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        CRUDServiceVerticle serviceVerticle = new CRUDServiceVerticle();
        serviceVerticle.addClassMapping("touchpoints", StationaryTouchpoint.class);

        vertx.deployVerticle(serviceVerticle,stringAsyncResult -> {
            vertx.deployVerticle(CRUDVerticle.class.getName(),stringAsyncResult1 -> {}
                    /*context.asyncAssertSuccess()*/);
        });
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
//        final Async async = context.async();
//
//        // create request
//        StationaryTouchpoint tp = new StationaryTouchpoint(-1,"dorem",new Address("lipsum","-42","olor","adispiscing"));
//
//        String json = Json.encodePrettily(tp);
//        logger.info("about to send: " + json);
//
//        vertx.createHttpClient().post(8080,"localhost","/api/touchpoints",
//                response -> {
//                    response.handler(body -> {
//                        context.assertTrue(body.toString().contains("Hello"));
//                        async.complete();
//                    });
//                }).putHeader("Content-Length",String.valueOf(json.getBytes().length)).write(/*json*/"lorem ipsum dolor sit amet").end();
//
//        logger.info("sleeping...");
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }




}


