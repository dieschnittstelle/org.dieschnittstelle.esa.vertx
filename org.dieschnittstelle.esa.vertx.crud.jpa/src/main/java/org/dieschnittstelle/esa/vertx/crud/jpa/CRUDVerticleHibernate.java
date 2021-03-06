package org.dieschnittstelle.esa.vertx.crud.jpa;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.hibernate.HibernateService;
import io.vertx.hibernate.queries.FindBy;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDRequest;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.api.POJOMessageCodec;

import java.util.List;

/**
 * Created by master on 27.05.16.
 */
public class CRUDVerticleHibernate<T> extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(CRUDVerticleHibernate.class);

    private HibernateService service;

    public CRUDVerticleHibernate() {
        logger.info("<constructor>");
    }

    public void start(Future<Void> fut) {

        logger.info("start(): registering codecs...");

        startHibernateServiceAndRegisterHandlers(fut);

    }

    private void startHibernateServiceAndRegisterHandlers(Future<Void> fut) {
        JsonObject config = new JsonObject();
        config.put("persistence-unit", "crm_erp_PU");
        this.service = new HibernateService(vertx,config);
        Future<Void> future = Future.future();
        future.setHandler(result -> {
            if (result.cause() != null)  {
                result.cause().printStackTrace();
            }
            else {
                registerHandlers();
            }
            logger.info("startup complete");
            fut.complete();
        });
        service.start(future);

    }

    private void registerHandlers() {

        Handler<Message<CRUDRequest<T>>> myHandler = new Handler<Message<CRUDRequest<T>>>() {
            public void handle(Message<CRUDRequest<T>> message) {
                logger.info("handle(): start, replyAddress: " + message.replyAddress());

                Future<Object> future = Future.future();
                future.setHandler(objectAsyncResult -> {
                    CRUDResult<T> result =  (CRUDResult<T>)objectAsyncResult.result();
                    logger.info("handle(): got result: " + result);
                    message.reply(result);
                });

                handleCRUDRequest(message,future);

                logger.info("handle(): returning");
            }
        };

        vertx.eventBus().consumer(CRUDRequest.class.getName(),myHandler);
        vertx.eventBus().consumer(CRUDRequest.class.getName()+"."+CRUDVerticleHibernate.class.getSimpleName(),myHandler);

        logger.info("registered handler");

    }

    private void handleCRUDRequest(Message<CRUDRequest<T>> message, Future<Object> fut) {
        logger.info("handleCRUDRequest(): " + message);
        CRUDRequest<T> request = message.body();

        switch (request.getOperation()) {
            case CREATE:
                create(request,fut);
                break;
            case READ:
                read(request,fut);
                break;
            case READALL:
                readAll(request,fut);
                break;
            default:
                logger.error("cannot handle CRUDRequest with operation " + request.getOperation() + ". Operation is not yet supported");
        }
    }

    private int count;

    private synchronized int incrementCount() {
        return count++;
    }

    private void create(CRUDRequest<T> request,Future<Object> fut) {
        logger.info("create(): " + request.getEntity() + ", service is: " + service);

        int ccount = incrementCount();
        System.out.println(System.currentTimeMillis() + ": " + this + "@" +  Thread.currentThread() + ": start create " + ccount);

        service.withinTransaction(em -> {
            logger.info("calling persist()");
            em.persist(request.getEntity());
            return request.getEntity();
        }, res -> {
            if (res.failed()) {
                res.cause().printStackTrace();
            }
            T result = res.result();
            logger.info("create(): got result: " + result);

            System.out.println(System.currentTimeMillis() + ": " + this + "@" +  Thread.currentThread() + ": end create " + ccount);
            fut.complete(new CRUDResult<T>(res.result()));
        });

//        message.reply(new CRUDResult<T>(request.getEntity()));
    }

    private void read(CRUDRequest<T> request,Future<Object> fut) {
        logger.info("read(): " + request.getEntityId());

        service.withEntityManager(em -> {
            logger.info("calling find()");
            FindBy<T, Long> fb = null;
            try {
                fb = new FindBy<T, Long>(request.getEntityClass(), em);
                T result = fb.find("id", request.getEntityId());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, res -> {
            if (res.failed()) {
                res.cause().printStackTrace();
            }
            T result = res.result();
            logger.info("read(): got result: " + result);
            fut.complete(new CRUDResult<T>(result));
        });
    }

    private void readAll(CRUDRequest<T> request,Future<Object> fut) {
        logger.info("readAll(): " + request.getEntityClass());

        service.withEntityManager(em -> {
            logger.info("calling find()");
            FindBy<T, Long> fb = null;
            try {
                fb = new FindBy<T, Long>(request.getEntityClass(), em);
                List<T> result = (List<T>)fb.findAll();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, res -> {
            if (res.failed()) {
                res.cause().printStackTrace();
            }
            List<T> result = res.result();
            logger.info("read(): got result: " + result);
            fut.complete(new CRUDResult<T>(result));
        });
    }


}
