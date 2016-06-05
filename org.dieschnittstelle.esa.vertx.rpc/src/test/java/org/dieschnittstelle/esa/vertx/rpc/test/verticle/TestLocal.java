package org.dieschnittstelle.esa.vertx.rpc.test.verticle;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import org.dieschnittstelle.esa.vertx.rpc.Local;
import org.dieschnittstelle.jee.esa.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

/**
 * Created by master on 05.06.16.
 */
@Local
public interface TestLocal {

    public void createTouchpoint(AbstractTouchpoint tp, Future<AbstractTouchpoint> callback);

}
