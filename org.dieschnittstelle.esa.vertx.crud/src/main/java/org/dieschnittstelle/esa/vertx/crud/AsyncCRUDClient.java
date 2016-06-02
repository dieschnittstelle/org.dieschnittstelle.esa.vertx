package org.dieschnittstelle.esa.vertx.crud;

import io.vertx.core.Future;

import java.util.List;

/**
 * Created by master on 31.05.16.
 */
public interface AsyncCRUDClient<T> {

    public void create(T entity, Future<CRUDResult<T>> callback);

    public void read(Class<T> entityclass, long entityid, Future<CRUDResult<T>> callback);

    public void readAll(Class<T> entityclass, Future<CRUDResult<T>> callback);

    public void update(long entityid, T entitydata, Future<CRUDResult<T>> callback);

    public void delete(Class<T> entityclass, long entityid, Future<CRUDResult<T>> callback);

}
