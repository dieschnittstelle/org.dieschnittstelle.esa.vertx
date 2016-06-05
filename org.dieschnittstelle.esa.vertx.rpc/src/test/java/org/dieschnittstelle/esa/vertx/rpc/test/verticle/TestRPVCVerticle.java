package org.dieschnittstelle.esa.vertx.rpc.test.verticle;

import io.vertx.core.Future;
import org.dieschnittstelle.esa.vertx.rpc.AbstractRPCVerticle;
import org.dieschnittstelle.jee.esa.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

import org.apache.log4j.Logger;

/**
 * Created by master on 05.06.16.
 */
public class TestRPVCVerticle extends AbstractRPCVerticle implements TestRemote, TestLocal {

    protected static Logger logger = Logger.getLogger(TestRPVCVerticle.class);

    public void createTouchpoint(AbstractTouchpoint tp, Future<AbstractTouchpoint> callback) {
        logger.info("createTouchpoint(): " + tp);

        callback.complete(tp);
    }
}
