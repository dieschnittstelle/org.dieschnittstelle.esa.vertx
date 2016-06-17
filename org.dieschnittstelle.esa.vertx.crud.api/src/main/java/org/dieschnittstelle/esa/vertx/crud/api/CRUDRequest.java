package org.dieschnittstelle.esa.vertx.crud.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by master on 27.05.16.
 */
public class CRUDRequest<T> {

    private static EntityMarshaller marshaller = new EntityMarshaller(new JsonObjectEntityMarshallerDelegate());

    /* setup will be used to prepare the data source (in js mongodb client collections will not be created on save, at least not by default) */
    public enum Operation {
        CREATE, READALL, READ, UPDATE, DELETE, DELETEALL, SETUP;
    }

    protected static Logger logger = Logger.getLogger(CRUDRequest.class);

    protected static ObjectMapper mapper = new ObjectMapper();

    private T entity;

    private long entityId;
    private String entityIdString;
    private Class<T> entityClass;

    private Operation operation;

    public CRUDRequest(Operation operation,T entity) {
        this.operation = operation;
        this.entity = entity;
    }

    public CRUDRequest(Operation operation,long entityId,T entity) {
        this(operation,entity);
        this.entityId = entityId;
    }

    public CRUDRequest(Operation operation,String entityId,T entity) {
        this(operation,entity);
        Object parsedid = parseEntityid(entityId);
        if (parsedid instanceof String) {
            this.entityIdString = (String)parsedid;
        }
        else {
            this.entityId = (long)parsedid;
        }
    }

    public CRUDRequest(Operation operation,long entityId) {
        this.operation = operation;
        this.entityId = entityId;
    }

    public CRUDRequest(Operation operation,String entityId) {
        this.operation = operation;
        Object parsedid = parseEntityid(entityId);
        if (parsedid instanceof String) {
            this.entityIdString = (String)parsedid;
        }
        else {
            this.entityId = (long)parsedid;
        }
    }

    public CRUDRequest(Operation operation,Class<T> entityClass) {
        this.operation = operation;
        this.entityClass = entityClass;
    }

    public CRUDRequest(Operation operation,Class<T> entityClass,long entityId) {
        this.operation = operation;
        this.entityId = entityId;
        this.entityClass = entityClass;
    }

    public CRUDRequest(Operation operation,Class<T> entityClass,String entityId) {
        this.operation = operation;
        this.entityClass = entityClass;
        Object parsedid = parseEntityid(entityId);
        if (parsedid instanceof String) {
            this.entityIdString = (String)parsedid;
        }
        else {
            this.entityId = (long)parsedid;
        }
    }

    private Object parseEntityid(String entityid) {
        try {
            return Long.parseLong(entityid);
        }
        catch (NumberFormatException nfe) {
            return entityid;
        }
    }

    public CRUDRequest(Operation operation) {
        this.operation = operation;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityIdString() {
        return entityIdString;
    }

    public void setEntityIdString(String entityIdString) {
        this.entityIdString = entityIdString;
    }

    public JsonObject toJsonObject() throws InvocationTargetException, IllegalAccessException {
        // TODO: it is suboptimal to creare a marshaller for each instance we want
        return ((JsonObject) marshaller.marshal(null, this, null)).getJsonObject("data");
    }

    public JsonObject toJsonObject(Object obj) throws InvocationTargetException, IllegalAccessException {
        // TODO: it is suboptimal to creare a marshaller for each instance we want
        return ((JsonObject) marshaller.marshal(null, obj, null)).getJsonObject("data");
    }

    public String toJsonString(Object obj) throws JsonProcessingException {
        try {
            return mapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            logger.error("toJsonString(): got exception: " + e,e);
            throw e;
        }
    }

    /*
     * we allow to create a result for sending back typed objects out of javascript verticles
     */
    public <T> CRUDResult<T> createResult() {
        return new CRUDResult<T>();
    }

    public static Object unmarshal(String jsonString,Class klass) {
        return Json.decodeValue(jsonString,klass);
    }

}
