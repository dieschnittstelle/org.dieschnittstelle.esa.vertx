package org.dieschnittstelle.esa.vertx.webapi;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.AsyncCRUDClient;
import org.dieschnittstelle.esa.vertx.crud.api.VerticleCRUDClient;

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
    private static final String PARAM_CRUDPROVIDER = "crudprovider";
    private static final String PARAM_BROADCAST = "broadcast";

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

        // this is for serving static resources
        router.route("/ui/*").handler(StaticHandler.create("ui"));

        // the following line is required in order for any of the below methods to access the request body
        router.route().handler(BodyHandler.create());
        router.route(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "*").handler(BodyHandler.create());
        router.post(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS).handler(this::create);
        router.get(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "/:" + PARAM_ENTITYID).handler(this::read);
        router.get(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS).handler(this::readAll);
        router.put(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "/:" + PARAM_ENTITYID).handler(this::update);
        router.delete(BASE_API_PATH + "/:" + PARAM_ENTITYCLASS + "/:" + PARAM_ENTITYID).handler(this::delete);

//        router.route("/").handler(routingContext -> {
//            HttpServerResponse response = routingContext.response();
//            response
//                    .putHeader("content-type", "text/html")
//                    .end("<h1>Hello from Vertx Webapi Impl</h1>");
//        });

    }


    private int count;

    private synchronized int incrementCount() {
        return count++;
    }

    /*
     * these are the generic crud methods that are bound to the routes
     */
    public void create(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        logger.info("create(): class: " + entityclass);
        prepareClient(routingContext);

        int ccount = incrementCount();

        System.out.println(System.currentTimeMillis() + ": " + this + "@" +  Thread.currentThread() + ": start create " + ccount);

        String body = routingContext.getBodyAsString();

        logger.info("create(): body is: " + body);

        Object entity = Json.decodeValue(body,
                classMap.get(entityclass));

        Future<Object> callback = Future.future();
        callback.setHandler(asyncResult -> {
            Object crudresult = asyncResult.result();
            logger.info("create(): got result: " + crudresult);

            System.out.println(System.currentTimeMillis() + ": " + this + "@" +  Thread.currentThread() + ": end create " + ccount);

            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(201).end(Json.encodePrettily(crudresult));
        });
        crudClient.create(entity,callback);
    }

    public void read(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        String entityid = routingContext.request().getParam(PARAM_ENTITYID);
        logger.info("read(): class: " + entityclass);
        logger.info("read(): id: " + entityid);
        prepareClient(routingContext);

        Future<Object> callback = Future.future();
        callback.setHandler(asyncResult -> {
            Object crudresult = asyncResult.result();
            logger.info("read(): got result: " + crudresult);
            // create a crud result, using the long value and serialise it as json object
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).end(Json.encodePrettily(crudresult));
        });

        crudClient.read(classMap.get(entityclass),entityid,callback);
    }

    public void readAll(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        logger.info("readAll(): class: " + entityclass);
        prepareClient(routingContext);

        Future<Object> callback = Future.future();
        callback.setHandler(asyncResult -> {
            Object crudresult = asyncResult.result();
            logger.info("read(): got result: " + crudresult);
            // create a crud result, using the long value and serialise it as json object
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).end(Json.encodePrettily(crudresult));
        });

        crudClient.readAll(classMap.get(entityclass),callback);
    }

    public void update(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        String entityid = routingContext.request().getParam(PARAM_ENTITYID);
        logger.info("update(): class: " + entityclass);
        logger.info("update(): id: " + entityid);
        prepareClient(routingContext);

        routingContext.response().setStatusCode(405).end();
    }

    public void delete(RoutingContext routingContext) {
        String entityclass = routingContext.request().getParam(PARAM_ENTITYCLASS);
        String entityid = routingContext.request().getParam(PARAM_ENTITYID);
        logger.info("delete(): class: " + entityclass);
        logger.info("delete(): id: " + entityid);
        prepareClient(routingContext);

        routingContext.response().setStatusCode(405).end();
    }

    private void prepareClient(RoutingContext ctx) {
        String crudprovider = ctx.request().getParam(PARAM_CRUDPROVIDER);
        String broadcast = ctx.request().getParam(PARAM_BROADCAST);
        logger.info("crudprovider: " + crudprovider);
        logger.info("broadcast: " + broadcast);

        if (crudprovider != null && !"".equals(crudprovider.trim())) {
            crudClient.setCrudprovider(crudprovider);
        }
        else {
            crudClient.setCrudprovider(null);
        }
        if (broadcast != null && !"".equals(broadcast.trim())) {
            crudClient.setBroadcast(Boolean.parseBoolean(broadcast));
        }
        else {
            crudClient.setBroadcast(false);
        }
    }


    public void addClassMapping(String classname,Class klass) {
        this.classMap.put(classname,klass);
    }

}
