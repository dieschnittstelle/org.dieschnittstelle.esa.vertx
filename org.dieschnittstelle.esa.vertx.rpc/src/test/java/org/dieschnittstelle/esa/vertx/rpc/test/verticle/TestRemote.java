package org.dieschnittstelle.esa.vertx.rpc.test.verticle;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import org.dieschnittstelle.esa.vertx.rpc.Remote;
import org.dieschnittstelle.jee.esa.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;

/**
 * Created by master on 05.06.16.
 */
@Remote
public interface TestRemote {

    public void createTouchpoint(AbstractTouchpoint tp, Future<AbstractTouchpoint> callback);

}
