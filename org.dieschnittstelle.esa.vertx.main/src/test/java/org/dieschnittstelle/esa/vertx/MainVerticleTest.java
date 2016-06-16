package org.dieschnittstelle.esa.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.api.POJOMessageCodec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by master on 16.06.16.
 */
@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    protected static Logger logger = Logger.getLogger(MainVerticleTest.class);

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        vertx.eventBus().registerDefaultCodec(CRUDRequest.class,new POJOMessageCodec(CRUDRequest.class));
        vertx.eventBus().registerDefaultCodec(CRUDResult.class,new POJOMessageCodec(CRUDResult.class));

        // deploy the verticle as a worker?
        vertx.deployVerticle("js/CRUDVerticleJS.js",new DeploymentOptions(),
                context.asyncAssertSuccess());
    }

    @Test
    public void test() {
    }
}
