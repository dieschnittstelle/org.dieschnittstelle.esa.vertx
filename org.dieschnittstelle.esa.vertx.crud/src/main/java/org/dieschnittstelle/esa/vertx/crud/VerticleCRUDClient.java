package org.dieschnittstelle.esa.vertx.crud;

import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by master on 31.05.16.
 */
public class VerticleCRUDClient<T> implements AsyncCRUDClient<T> {

    protected static Logger logger = Logger.getLogger(VerticleCRUDClient.class);

    private Vertx vertx;

    public VerticleCRUDClient() {
        this.vertx = Vertx.vertx();
    }

    public VerticleCRUDClient(Vertx vertx) {
        this.vertx = vertx;
    }


    public void create(T entity, Future<CRUDResult<T>> callback) {

        logger.info("create(): " + entity + "/" + callback);

        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.CREATE,entity);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                callback.complete(result.result().body());
            }
        });

    }

    public void read(Class<T> entityclass, long entityid, Future<CRUDResult<T>> callback) {

        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READ,entityclass,entityid);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                callback.complete(result.result().body());
            }
        });

    }

    public void readAll(Class<T> entityclass, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READALL,entityclass);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                callback.complete(result.result().body());
            }
        });

    }

    public void update(long entityid, T entitydata, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.UPDATE,entityid,entitydata);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                callback.complete(result.result().body());
            }
        });

    }

    public void delete(Class<T> entityclass, long entityid, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.DELETE,entityclass,entityid);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                callback.complete(result.result().body());
            }
        });

    }

}
