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


    public void create(T entity, Future<T> callback) {

        logger.info("create(): " + entity + "/" + callback);

        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.CREATE,entity);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                logger.info("create(): " + result + "/" + result.result());
                //Long entityId = result.result().body().getEntityId();
                callback.complete((T)/*entityId*/ result.result().body());
            }
        });

    }

    public void read(Class<T> entityclass, long entityid, Future<T> callback) {

        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READ,entityclass,entityid);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                T read = result.result().body().getEntity();
                callback.complete(read);
            }
        });

    }

    public void readAll(Class<T> entityclass, Future<List<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READALL,entityclass);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                List<T> readall = result.result().body().getEntityList();
                callback.complete(readall);
            }
        });

    }

    public void update(long entityid, T entitydata, Future<Integer> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.UPDATE,entityid,entitydata);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                int rowsChanged = result.result().body().getRowsChanged();
                callback.complete(rowsChanged);
            }
        });

    }

    public void delete(Class<T> entityclass, long entityid, Future<Integer> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.DELETE,entityclass,entityid);

        vertx.eventBus().send(CRUDRequest.class.getName(), request, new AsyncResultHandler<Message<CRUDResult<T>>>() {
            @Override
            public void handle(AsyncResult<Message<CRUDResult<T>>> result) {
                int rowsChanged = result.result().body().getRowsChanged();
                callback.complete(rowsChanged);
            }
        });

    }

}
