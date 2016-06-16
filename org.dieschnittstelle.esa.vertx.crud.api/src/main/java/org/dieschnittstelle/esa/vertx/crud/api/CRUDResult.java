package org.dieschnittstelle.esa.vertx.crud.api;

import io.vertx.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by master on 27.05.16.
 */
public class CRUDResult<T> {

    private static EntityMarshaller marshaller = new EntityMarshaller(new JsonObjectEntityMarshallerDelegate());

    private T entity;
    private List<T> entityList = new ArrayList<T>();
    private long entityId;

    public CRUDResult() {

    }

    public CRUDResult(long entityId) {
        this.entityId = entityId;
    }

    public CRUDResult(T entity) {
        this.entity = entity;
    }

    public CRUDResult(int rowsChanged) {
        this.rowsChanged = rowsChanged;
    }

    public CRUDResult(List<T> entityList) {
        this.entityList = entityList;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }

    public void addEntity(T entity) {
        this.entityList.add(entity);
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public int getRowsChanged() {
        return rowsChanged;
    }

    public void setRowsChanged(int rowsChanged) {
        this.rowsChanged = rowsChanged;
    }

    private int rowsChanged;

    public JsonObject toJsonObject() throws InvocationTargetException, IllegalAccessException {
        // TODO: it is suboptimal to creare a marshaller for each instance we want
        return ((JsonObject) marshaller.marshal(null, this, null)).getJsonObject("data");
    }


}
