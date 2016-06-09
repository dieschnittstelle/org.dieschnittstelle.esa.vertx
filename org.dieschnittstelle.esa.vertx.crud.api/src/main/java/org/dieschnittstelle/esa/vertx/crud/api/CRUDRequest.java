package org.dieschnittstelle.esa.vertx.crud.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * Created by master on 27.05.16.
 */
public class CRUDRequest<T> {

    public enum Operation {
        CREATE, READALL, READ, UPDATE, DELETE;
    }

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
        this.entityIdString = entityId;
    }

    public CRUDRequest(Operation operation,long entityId) {
        this.operation = operation;
        this.entityId = entityId;
    }

    public CRUDRequest(Operation operation,String entityId) {
        this.operation = operation;
        this.entityIdString = entityId;
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
        this.entityIdString = entityId;
        this.entityClass = entityClass;
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

}
