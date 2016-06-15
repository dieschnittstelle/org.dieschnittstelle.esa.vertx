package org.dieschnittstelle.esa.vertx.crud.mongod;

import java.util.ArrayList;
import java.util.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.api.EntityMarshaller;
import org.dieschnittstelle.esa.vertx.crud.api.JsonObjectEntityMarshallerDelegate;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by master on 27.05.16.
 */
public class CRUDVerticleMongod<T> extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(CRUDVerticleMongod.class);

    private MongoClient client;

    private EntityMarshaller marshaller;

    public CRUDVerticleMongod() {
        logger.info("<constructor>");
    }

    public void start(Future<Void> fut) {
        logger.info("start()");

        String connectionUrl = "mongodb://%s:27017";
        String ipAddress = System.getenv("MONGODB_PORT_27017_TCP_ADDR");
        logger.info("start(): read ipAddress from environment variables: " + ipAddress);
        if (ipAddress == null || "".equals(ipAddress.trim())) {
            ipAddress = "localhost";
            logger.info("start(): default ipAddress to: " + ipAddress);
        }
        connectionUrl = String.format(connectionUrl,ipAddress);
        logger.info("using connectionUrl: " + connectionUrl);

        JsonObject config = new JsonObject()
                        .put("http.port", 28017)
                        .put("db_name", "crm_erp_db")
                        .put("connection_string",
                                connectionUrl);

        client = MongoClient.createShared(vertx, config);
        marshaller = new EntityMarshaller(new JsonObjectEntityMarshallerDelegate());

        registerHandlers();
        fut.complete();
    }

    private void registerHandlers() {

        Handler<Message<CRUDRequest<T>>> myHandler = new Handler<Message<CRUDRequest<T>>>() {
            public void handle(Message<CRUDRequest<T>> message) {
                logger.info("handle(): start, replyAddress: " + message.replyAddress());

                Future<Object> future = Future.future();
                future.setHandler(objectAsyncResult -> {
                    CRUDResult<T> result = (CRUDResult<T>) objectAsyncResult.result();
                    logger.info("handle(): got result: " + result);
                    message.reply(result);
                });

                handleCRUDRequest(message, future);

                logger.info("handle(): returning");
            }
        };

        vertx.eventBus().consumer(CRUDRequest.class.getName(), myHandler);
        vertx.eventBus().consumer(CRUDRequest.class.getName()+"."+CRUDVerticleMongod.class.getSimpleName(), myHandler);

        logger.info("registered handler");

    }

    private void handleCRUDRequest(Message<CRUDRequest<T>> message, Future<Object> fut) {
        logger.info("handleCRUDRequest(): " + message);
        CRUDRequest<T> request = message.body();

        switch (request.getOperation()) {
            case CREATE:
                create(request, fut);
                break;
            case READ:
                read(request, fut);
                break;
            case READALL:
                readAll(request, fut);
                break;
            default:
                logger.error("cannot handle CRUDRequest with operation " + request.getOperation() + ". Operation is not yet supported");
        }
    }

    private int count;

    private synchronized int incrementCount() {
        return count++;
    }


    private void create(CRUDRequest<T> request, Future<Object> fut)  {
        logger.debug("create(): " + request.getEntity() + ", client is: " + client);

        int ccount = incrementCount();

//        if (ccount % 50 == 0) {
//            try {
//                Thread.currentThread().sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        System.out.println(System.currentTimeMillis() + ": " + this + "@" +  Thread.currentThread() + ": start create " + ccount);

        try {
            T entity = request.getEntity();
            JsonObject obj = ((JsonObject) marshaller.marshal(null, entity, null)).getJsonObject("data");

            logger.debug("marshalled JsonObject: " + obj);

            // for the tome being, we save the entity in a collection that has the class name, however, this should be controlled via the crud request
            client.save(request.getEntity().getClass().getName(), obj, res -> {

                if (res.succeeded()) {
                    String id = res.result();
                    logger.info("create(): assigned id: " + id + ", obj is: " + obj + ", id on obj is: " + obj.getString("_id"));

                    try {
                        entity.getClass().getMethod("set_id", new Class[]{String.class}).invoke(entity, id);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    System.out.println(System.currentTimeMillis() + ": " + this + "@" +  Thread.currentThread() + ": end create " + ccount);

                    fut.complete(new CRUDResult(entity));

                } else {
                    res.cause().printStackTrace();
                }

            });

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void read(CRUDRequest<T> request, Future<Object> fut) {
        logger.info("read(): " + request.getEntityClass().getName());
        logger.info("read(): " + request.getEntityIdString());
        JsonObject query = new JsonObject();
        query.put("_id",request.getEntityIdString());

        client.findOne(request.getEntityClass().getName(), query, null, res -> {

            if (res.succeeded()) {
                JsonObject obj = res.result();
                if (logger.isDebugEnabled()) {
                    logger.debug("read obj for id " + request.getEntityIdString() + ": " + obj);
                }
                fut.complete(new CRUDResult<T>(Json.decodeValue(obj.encode(),request.getEntityClass())));
            } else {

                res.cause().printStackTrace();

            }

        });

    }

    private void readAll(CRUDRequest<T> request, Future<Object> fut) {
        logger.info("readAll(): " + request.getEntityClass().getName());
        JsonObject query = new JsonObject();

        client.find(request.getEntityClass().getName(), query, res -> {

            if (res.succeeded()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("readAll(): got json: " + res.result());
                }

                List<T> objs = new ArrayList<T>();
                if (logger.isDebugEnabled()) {
                    logger.debug("readAll(): result is: " + res.result());
                }
                for (JsonObject json : res.result()) {
                    objs.add(Json.decodeValue(json.encode(),request.getEntityClass()));
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("readAll(): objs: " + objs);
                }
                fut.complete(new CRUDResult<T>(objs));
            } else {

                res.cause().printStackTrace();

            }

        });

    }


}
