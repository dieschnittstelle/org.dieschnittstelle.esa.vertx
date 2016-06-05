package org.dieschnittstelle.esa.vertx.crud;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.hibernate.HibernateService;
import io.vertx.hibernate.queries.FindBy;
import org.apache.log4j.Logger;

/**
 * Created by master on 27.05.16.
 */
public class CRUDVerticle<T> extends AbstractVerticle {

    protected static Logger logger = Logger.getLogger(CRUDVerticle.class);

    private HibernateService service;

    public CRUDVerticle() {
        logger.info("<constructor>");
    }

    public void start(Future<Void> fut) {

        logger.info("start(): registering codecs...");

        // TODO: registration could be done outside in some main verticle, but would then need to be called by the test cases
        vertx.eventBus().registerDefaultCodec(CRUDRequest.class,new POJOMessageCodec(CRUDRequest.class));
        vertx.eventBus().registerDefaultCodec(CRUDResult.class,new POJOMessageCodec(CRUDResult.class));

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
            default:
                logger.error("cannot handle CRUDRequest with operation " + request.getOperation() + ". Operation is not yet supported");
        }
    }

    private void create(CRUDRequest<T> request,Future<Object> fut) {
        logger.info("create(): " + request.getEntity() + ", service is: " + service);

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


}
