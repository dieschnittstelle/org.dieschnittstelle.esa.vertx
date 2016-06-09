package org.dieschnittstelle.esa.vertx.crud.api;

import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.apache.log4j.Logger;

/**
 * Created by master on 31.05.16.
 */
public class VerticleCRUDClient<T> implements AsyncCRUDClient<T> {

    protected static Logger logger = Logger.getLogger(VerticleCRUDClient.class);

    private Vertx vertx;

    // whether we shall broadcast requests or send them 1:1 to the receivers
    private boolean broadcast;

    // if we want to address a particular verticle we can specify this attribute which will be added to the message id
    private String crudprovider;

    public VerticleCRUDClient() {
        this.vertx = Vertx.vertx();
    }

    public VerticleCRUDClient(Vertx vertx) {
        this.vertx = vertx;
    }

    public void create(T entity, Future<CRUDResult<T>> callback) {
        logger.info("create(): " + entity + "/" + callback);
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.CREATE,entity);
        sendOrPublishRequest(request,callback);
    }

    public void read(Class<T> entityclass, long entityid, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READ,entityclass,entityid);
        sendRequest(request,callback);
    }

    public void readAll(Class<T> entityclass, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READALL,entityclass);
        sendRequest(request,callback);
    }

    public void update(long entityid, T entitydata, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.UPDATE,entityid,entitydata);
        sendOrPublishRequest(request,callback);
    }

    public void delete(Class<T> entityclass, long entityid, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.DELETE,entityclass,entityid);
        sendOrPublishRequest(request,callback);
    }

    /*
     * the methods that actually do the job
     */

    private void sendRequest(CRUDRequest<T> request, Future<CRUDResult<T>> callback) {
        logger.info("sendRequest(): crudprovider is: " + crudprovider);

        vertx.eventBus().send(getMessageAddressee(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                callback.complete(result.result().body());
            }
        });
    }

    private void sendOrPublishRequest(CRUDRequest<T> request, Future<CRUDResult<T>> callback) {
        logger.info("sendRequest(): crudprovider is: " + crudprovider);
        logger.info("sendRequest(): broadcast is: " + broadcast);

        if (broadcast) {
            vertx.eventBus().publish(CRUDRequest.class.getName(),request);
            callback.complete();
        }
        else {
            sendRequest(request,callback);
        }
    }


    public void setCrudprovider(String crudprovider) {
        this.crudprovider = crudprovider;
    }

    @Override
    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public String getCrudprovider() {
        return this.crudprovider;
    }

    private String getMessageAddressee() {
        String addressee = CRUDRequest.class.getName() + (crudprovider != null && !"".equals(crudprovider) ? ("." + crudprovider) : "");
        logger.info("messageAddressee: " + addressee);
        return addressee;
    }

}
