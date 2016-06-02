package org.dieschnittstelle.esa.webapi.client;

import retrofit.http.*;

import java.util.List;

/**
 * Created by master on 01.06.16.
 */
public interface CRUDWebapi<T> {

    @POST("/{entitytype}")
    public CRUDResult<T> create(@Path("entitytype") String entitytype, @Body T entity);

    @GET("/{entitytype}/{entityid}")
    public CRUDResult<T> read(@Path("entitytype") String entitytype,@Path("entityid") long entityid);

    @GET("/{entitytype}")
    public CRUDResult<T> readall(@Path("entitytype") String entitytype);

    @PUT("/{entitytype}/{entityid}")
    public CRUDResult<T> update(@Path("entitytype") String entitytype,@Path("entityid") long entityid, @Body T update);

    @DELETE("/{entitytype}/{entityid}")
    public CRUDResult<T> delete(@Path("entitytype") String entitytype,@Path("entityid") long entityid);

}
