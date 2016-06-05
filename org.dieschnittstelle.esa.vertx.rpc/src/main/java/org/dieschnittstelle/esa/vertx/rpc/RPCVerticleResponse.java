package org.dieschnittstelle.esa.vertx.rpc;

/**
 * Created by master on 05.06.16.
 */
public class RPCVerticleResponse {

    private Object result;

    public RPCVerticleResponse(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return this.result;
    }

}



