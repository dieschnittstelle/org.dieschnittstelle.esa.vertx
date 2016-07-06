package org.dieschnittstelle.esa.webapi.client;

import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by master on 01.06.16.
 *
 * TODO: as in the case of retrofit we should use make this class generic and use a custom object mapper
 */
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CRUDWebapiJAXRS<T> {

    @POST
    @Path("/{entitytype}")
    public CRUDResult<StationaryTouchpointDoc> create(@PathParam("entitytype") String entitytype, StationaryTouchpointDoc entity, @QueryParam("crudprovider") String crudprovider, @QueryParam("broadcast") boolean broadcast);

    @GET
    @Path("/{entitytype}/{entityid}")
    public CRUDResult<StationaryTouchpointDoc> read(@PathParam("entitytype") String entitytype, @PathParam("entityid") long entityid, @QueryParam("crudprovider") String crudprovider);

    @GET
    @Path("/{entitytype}")
    public CRUDResult<StationaryTouchpointDoc> readall(@PathParam("entitytype") String entitytype, @QueryParam("crudprovider") String crudprovider);

    @PUT
    @Path("/{entitytype}/{entityid}")
    public CRUDResult<StationaryTouchpointDoc> update(@PathParam("entitytype") String entitytype, @PathParam("entityid") long entityid, StationaryTouchpointDoc update, @QueryParam("crudprovider") String crudprovider);

    @DELETE
    @Path("/{entitytype}/{entityid}")
    public CRUDResult<StationaryTouchpointDoc> delete(@PathParam("entitytype") String entitytype, @PathParam("entityid") long entityid, @QueryParam("crudprovider") String crudprovider);

}
