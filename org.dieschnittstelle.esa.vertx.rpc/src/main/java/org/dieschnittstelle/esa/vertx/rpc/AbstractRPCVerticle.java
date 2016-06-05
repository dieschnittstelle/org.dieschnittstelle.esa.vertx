package org.dieschnittstelle.esa.vertx.rpc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.POJOMessageCodec;

import java.util.Arrays;

/**
 * Created by master on 05.06.16.
 */
public abstract class AbstractRPCVerticle extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(AbstractRPCVerticle.class);

    public void start(Future<Void> fut) {

        logger.info("start(): " + this);
        // TODO: registration should be done outside as this needs to be called only once and not for each extension of this class
        vertx.eventBus().registerDefaultCodec(RPCVerticleRequest.class,new POJOMessageCodec(RPCVerticleRequest.class));
        vertx.eventBus().registerDefaultCodec(RPCVerticleResponse.class,new POJOMessageCodec(RPCVerticleResponse.class));

        registerHandlers(fut);
    }


    private void registerHandlers(Future<Void> fut) {

        Handler<Message<RPCVerticleRequest>> myHandler = new Handler<Message<RPCVerticleRequest>>() {
            public void handle(Message<RPCVerticleRequest> message) {
                logger.info("handle(): start, replyAddress: " + message.replyAddress());

                Future<Object> future = Future.future();
                future.setHandler(objectAsyncResult -> {
                    RPCVerticleResponse result = new RPCVerticleResponse(objectAsyncResult.result());
                    logger.info("handle(): replying result: " + result);
                    message.reply(result);
                });

                RPCVerticleRequest request = message.body();
                logger.info("handle(): calling verticle method represented by request " + request);
                request.invoke(AbstractRPCVerticle.this,future);
            }
        };

        // we register the verticle as accessible via the local or remote interfaces
        logger.info("registerHandlers(): registering rpc verticle using interfaces: " + Arrays.asList(this.getClass().getInterfaces()));

        for (Class impl : this.getClass().getInterfaces()) {
            if (impl.isAnnotationPresent(Local.class)) {
                logger.info("registerHandlers(): verticle implements local interface: " + impl.getName());
                vertx.eventBus().consumer(RPCVerticleRequest.getRequestIdentifier(impl),myHandler);
            }
            else if (impl.isAnnotationPresent(Remote.class)) {
                logger.info("registerHandlers(): verticle implements remote interface: " + impl.getName());
                vertx.eventBus().consumer(RPCVerticleRequest.getRequestIdentifier(impl),myHandler);
            }
            else {
                logger.info("registerHandlers(): interface is not an rpc interface. Neither local nor remote annotation is present on: " + impl);
            }
        }

        logger.info("registered handler");
        fut.complete();

    }

}
