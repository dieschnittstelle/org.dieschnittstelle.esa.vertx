package org.dieschnittstelle.esa.vertx.webapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.AsyncCRUDClient;
import org.dieschnittstelle.esa.vertx.crud.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.VerticleCRUDClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by master on 31.05.16.
 *
 * following http://vertx.io/blog/some-rest-with-vert-x/
 */
public class CRUDServiceVerticle extends AbstractVerticle {

    /*
     * the parameters
     */
    private static final String BASE_API_PATH = "/api";
    private static final String PARAM_ENTITYCLASS = "entityclass";
    private static final String PARAM_ENTITYID = "id";

    protected static Logger logger = Logger.getLogger(CRUDServiceVerticle.class);

    /*
     * a map that associates unqualified classnames with class objects
     */
    private Map<String,Class<?>> classMap = new HashMap<String,Class<?>>();

    /*
     * a crud client
     */
    private AsyncCRUDClient crudClient;

    public void start(Future<Void> fut) {

        // instantiate the crud client
        crudClient = new VerticleCRUDClient(vertx);

        // Create a router object.
        Router router = Router.router(vertx);


        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );

        // the following line is required in order for any of the below methods to access the request body
        router.route().handler(BodyHandler.create());
        router.route(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "*").handler(BodyHandler.create());
        router.post(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS).handler(this::create);
        router.get(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "/:" + PARAM_ENTITYID).handler(this::read);
        router.get(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS).handler(this::readAll);
        router.put(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "/:" + PARAM_ENTITYID).handler(this::update);
        router.delete(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "/:" + PARAM_ENTITYID).handler(this::delete);

    }


    /*
     * these are the generic crud methods that are bound to the routes
     */
    public void create(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        logger.info("create(): class: " + entityclass);

        String body = routingContext.getBodyAsString();

        logger.info("create(): body is: " + body);

        Object entity = Json.decodeValue(body,
                classMap.get(entityclass));

        Future<Object> callback = Future.future();
        callback.setHandler(asyncResult -> {
            Object created = asyncResult.result();
            logger.info("got created entity: " + created);
            // create a crud result, using the long value and serialise it as json object
            CRUDResult result = new CRUDResult(created);
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(201).end(Json.encodePrettily(result));
        });
        crudClient.create(entity,callback);
    }

    public void read(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        String entityid = routingContext.request().getParam(PARAM_ENTITYID);
        logger.info("read(): class: " + entityclass);
        logger.info("read(): id: " + entityid);

        Future<Object> callback = Future.future();
        callback.setHandler(asyncResult -> {
            Object entity = asyncResult.result();
            // create a crud result, using the long value and serialise it as json object
            CRUDResult result = new CRUDResult(entity);
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).end(Json.encodePrettily(result));
        });

        crudClient.read(classMap.get(entityclass),Long.parseLong(entityid),callback);
    }

    public void readAll(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        logger.info("readAll(): class: " + entityclass);

        routingContext.response().setStatusCode(405).end();
    }

    public void update(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        String entityid = routingContext.request().getParam(PARAM_ENTITYID);
        logger.info("update(): class: " + entityclass);
        logger.info("update(): id: " + entityid);

        routingContext.response().setStatusCode(405).end();
    }

    public void delete(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        String entityid = routingContext.request().getParam(PARAM_ENTITYID);
        logger.info("delete(): class: " + entityclass);
        logger.info("delete(): id: " + entityid);

        routingContext.response().setStatusCode(405).end();
    }

    public void addClassMapping(String classname,Class klass) {
        this.classMap.put(classname,klass);
    }

}