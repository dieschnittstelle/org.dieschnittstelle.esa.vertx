package org.dieschnittstelle.esa.vertx.rpc;

import io.vertx.core.Future;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by master on 05.06.16.
 *
 * depending on whether we access a local or remote interface, we either pass the method object itself or the type information
 */
public class RPCVerticleRequest {

    public static Logger logger = Logger.getLogger(RPCVerticleRequest.class);

    private transient Method method;

    // the arguments of the method to be called except the last one, which is the callback function capturing the return value
    private Object[] methodArgs;

    private String methodName;

    private String[] methodArgTypes;

    private String verticleInterface;

    private boolean remoteCall;

    public RPCVerticleRequest(Class verticleInterface,Method method,Object[] methodArgs,boolean remoteCall) {
        this.remoteCall = remoteCall;
        this.verticleInterface = verticleInterface.getName();
        this.methodArgs = new Object[methodArgs.length-1];
        this.methodArgTypes = new String[methodArgs.length-1];
        if (/*remoteCall*/true) {
            this.methodName = method.getName();
            for (int i=0;i < (method.getParameterTypes().length -1);i++) {
                this.methodArgs[i] = methodArgs[i];
                this.methodArgTypes[i] = method.getParameterTypes()[i].getName();
            }
        }
        else {
            this.method = method;
        }
    }

    public void invoke(Object rpcVerticle,Future callbackFuture) {
        logger.info("invoke(): " + rpcVerticle);

        // determine the arguments
        Object[] invokeArgs = new Object[this.methodArgs.length+1];
        for (int i=0;i<this.methodArgs.length;i++) {
            invokeArgs[i] = this.methodArgs[i];
        }
        invokeArgs[this.methodArgs.length] = callbackFuture;

        // check whether we are local or remote
        if (false/*!this.remoteCall*/) {
            logger.info("invoke(): running local call on " + rpcVerticle);
            try {
                this.method.invoke(rpcVerticle,invokeArgs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                callbackFuture.complete(e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                callbackFuture.complete(e);
            }
        }
        else {
            try {
                logger.info("invoke(): running remote call on " + rpcVerticle);
                // we need to obtain the method given the method name and arg types
                Class[] invokeMethodArgTypes = new Class[this.methodArgTypes.length + 1];
                for (int i = 0; i < this.methodArgTypes.length; i++) {
                    invokeMethodArgTypes[i] = Class.forName(this.methodArgTypes[i]);
                }
                invokeMethodArgTypes[this.methodArgTypes.length] = Future.class;
                Method meth = rpcVerticle.getClass().getMethod(this.methodName, invokeMethodArgTypes);
                logger.info("invoke(): retrieved method: " + meth);
                meth.invoke(rpcVerticle,invokeArgs);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                callbackFuture.complete(e);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                callbackFuture.complete(e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                callbackFuture.complete(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                callbackFuture.complete(e);
            }
        }
    }

    public static String getRequestIdentifier(Class verticleInterface) {
        return RPCVerticleRequest.class.getName() + "4" + verticleInterface.getName();
    }

}
