package org.dieschnittstelle.esa.vertx.crud.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by master on 31.05.16.
 */
public class VerticleCRUDClient<T> implements AsyncCRUDClient<T> {

    protected static Logger logger = Logger.getLogger(VerticleCRUDClient.class);

    // we use a jackson object mapper - maybe we could centralise object mapper in some utilities singleton
    protected static ObjectMapper mapper = new ObjectMapper();

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

    public void read(Class<T> entityclass, String entityid, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READ,entityclass,entityid);
        sendRequest(request,callback);
    }

    public void readAll(Class<T> entityclass, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.READALL,entityclass);
        sendRequest(request,callback);
    }

    public void update(String entityid, T entitydata, Future<CRUDResult<T>> callback) {
        CRUDRequest<T> request = new CRUDRequest<T>(CRUDRequest.Operation.UPDATE,entityid,entitydata);
        sendOrPublishRequest(request,callback);
    }

    public void delete(Class<T> entityclass, String entityid, Future<CRUDResult<T>> callback) {
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
                callback.complete(createCRUDResult(request,result.result().body()));
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

    public static <T> CRUDResult<T> createCRUDResult(CRUDRequest<T> request,Object result) {

        // result either is a crud result or a string with a "raw" result, from which we need to create the result object
        if (result instanceof CRUDResult) {
            return (CRUDResult<T>) result;
        } else {
            logger.info(request.getOperation() + ": got a result string, will convert to the type we expect, given the request: " + result);
            CRUDResult<T> resultObj = new CRUDResult<T>();

            if (request.getOperation() == CRUDRequest.Operation.READ) {
                resultObj.setEntity(Json.decodeValue(String.valueOf(result), request.getEntityClass()));
            }
            else if (request.getOperation() == CRUDRequest.Operation.READALL) {
                List<T> entities = new ArrayList<T>();
                // TODO: currently, there is no straightforward way to simply convert a stringified json array to a list of domain objects... as we might not have the exact list type here...
                try {
                    // TODO: this is so terrible... what we would need is a list of still stringified json objects, i.e. a "shallow-parsed" result list
                    ArrayNode jsonarr = mapper.readValue(String.valueOf(result), ArrayNode.class);
                    for (JsonNode node : jsonarr) {
                       entities.add(mapper.readValue(mapper.writeValueAsString(node),request.getEntityClass()));
                    }
                    resultObj.setEntityList(entities);
                    return resultObj;
                }
                catch (Exception e) {
                    logger.error("got exception trying to read entities for CRUDResult: " + e,e);
                    return resultObj;
                }

//                List objs = Json.decodeValue(String.valueOf(result), List.class);
//                resultObj.setEntityList(objs);
            }
            else if (request.getOperation() == CRUDRequest.Operation.CREATE) {
                try {
                    Long id = Long.parseLong(String.valueOf(result));
                    try {
                        if (logger.isDebugEnabled()) {
                            logger.debug(request.getOperation() + ": trying to set id: " + id);
                        }
                        request.getEntity().getClass().getMethod("setId", new Class[]{String.class}).invoke(request.getEntity(), id);
                        resultObj.setEntity(request.getEntity());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } catch (NumberFormatException nfe) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(request.getOperation() + ": trying to set id: " + result);
                    }
                    try {
                        request.getEntity().getClass().getMethod("set_id", new Class[]{String.class}).invoke(request.getEntity(), String.valueOf(result));
                        resultObj.setEntity(request.getEntity());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                    throw new RuntimeException("The CRUD operation " + request.getOperation() + " is not supported so far.");
            }

            return resultObj;
        }

    }

}
