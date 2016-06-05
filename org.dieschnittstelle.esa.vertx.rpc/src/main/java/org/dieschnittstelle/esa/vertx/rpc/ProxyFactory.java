package org.dieschnittstelle.esa.vertx.rpc;

import io.vertx.core.Vertx;

import java.lang.reflect.Proxy;
import org.apache.log4j.Logger;

/**
 * Created by master on 05.06.16.
 */
public class ProxyFactory {

    protected static Logger logger = Logger.getLogger(ProxyFactory.class);

    private static ProxyFactory instance = new ProxyFactory();

    public static ProxyFactory getInstance() {
        return instance;
    }

    // TODO: need to find out how type parameters can be used in static methods - resteasy allows this, e.g.
    public <T> T createProxy(Class<T> verticleInterface,Vertx vertx) {

        logger.info("create(): " + verticleInterface);

        T proxy = (T)Proxy.newProxyInstance(verticleInterface.getClassLoader(),new Class[]{verticleInterface}, new RPCVerticleInvocationHandler(verticleInterface,vertx));

        return proxy;
    }

}
