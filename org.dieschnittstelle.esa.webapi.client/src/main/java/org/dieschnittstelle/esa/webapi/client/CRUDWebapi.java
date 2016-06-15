package org.dieschnittstelle.esa.webapi.client;

import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import retrofit.http.*;

/**
 * Created by master on 01.06.16.
 */
public interface CRUDWebapi<T> {

    @POST("/{entitytype}")
    public CRUDResult<T> create(@Path("entitytype") String entitytype, @Body T entity,@Query("crudprovider") String crudprovider,@Query("broadcast") boolean broadcast);

    @GET("/{entitytype}/{entityid}")
    public CRUDResult<T> read(@Path("entitytype") String entitytype,@Path("entityid") long entityid,@Query("crudprovider") String crudprovider);

    @GET("/{entitytype}")
    public CRUDResult<T> readall(@Path("entitytype") String entitytype,@Query("crudprovider") String crudprovider);

    @PUT("/{entitytype}/{entityid}")
    public CRUDResult<T> update(@Path("entitytype") String entitytype,@Path("entityid") long entityid, @Body T update,@Query("crudprovider") String crudprovider);

    @DELETE("/{entitytype}/{entityid}")
    public CRUDResult<T> delete(@Path("entitytype") String entitytype,@Path("entityid") long entityid,@Query("crudprovider") String crudprovider);

}
