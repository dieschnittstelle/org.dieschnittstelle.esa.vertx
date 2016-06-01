package org.dieschnittstelle.esa.vertx.webapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.log4j.Logger;

/**
 * Created by master on 27.05.16.
 *
 * following http://vertx.io/blog/my-first-vert-x-3-application/
 */
public class HelloVerticle extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(HelloVerticle.class);

    @Override
    public void start(Future<Void> fut) {

        logger.info("vertx: " + vertx);

        vertx
                .createHttpServer()
                .requestHandler(r -> {
                    r.response().end("<h1>Hello from my first " +
                            "Vert.x 3 application</h1>");
                })
                .listen(8887, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

}