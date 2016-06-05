package org.dieschnittstelle.esa.vertx.rpc;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by master on 05.06.16.
 */
public class RPCVerticleInvocationHandler<T> implements InvocationHandler {

    protected static Logger logger = Logger.getLogger(RPCVerticleInvocationHandler.class);

    private Class<T> verticleInterface;

    private Vertx vertx;

    private boolean local;

    public RPCVerticleInvocationHandler(Class<T> verticleInterface, Vertx vertx) {
        logger.info("<constructor>");
        this.verticleInterface = verticleInterface;
        this.vertx = vertx;
        this.local = this.verticleInterface.isAnnotationPresent(Local.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // the last argument must be a callback function
        Future callback = (Future)args[args.length-1];

        // we create an rpcrequest object
        RPCVerticleRequest request = new RPCVerticleRequest(this.verticleInterface,method,args,!this.local);

        String requestTypeId = RPCVerticleRequest.getRequestIdentifier(this.verticleInterface);

        logger.info("invoke(): about to send: " + request + ", using request identifier: " + requestTypeId);
        vertx.eventBus().send(requestTypeId,request,messageAsyncResult -> {
            RPCVerticleResponse response = (RPCVerticleResponse)messageAsyncResult.result().body();
            logger.info("invoke(): received: " + response);
            // we pass the content of the response to the callback
            callback.complete(response.getResult());
        });

        return null;
    }

}
