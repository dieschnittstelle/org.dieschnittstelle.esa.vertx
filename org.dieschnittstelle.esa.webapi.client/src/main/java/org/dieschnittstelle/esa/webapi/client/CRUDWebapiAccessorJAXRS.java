package org.dieschnittstelle.esa.webapi.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import java.util.List;

/**
 * Created by master on 01.06.16.
 */
public class CRUDWebapiAccessorJAXRS {

    private String entitypath;
    private Class<org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc> entityclass;
    private String baseUrl;
    private CRUDWebapiJAXRS<org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc> webapi;

    public CRUDWebapiAccessorJAXRS(String entitypath, Class<org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc> entityclass, String baseUrl) {
        this.entitypath = entitypath;
        this.entityclass = entityclass;
        this.baseUrl = baseUrl;

        ResteasyClient client = new ResteasyClientBuilder().build();
        client.register(JacksonJsonProvider.class);
        ResteasyWebTarget target = client.target(baseUrl);
        webapi = target.proxy(CRUDWebapiJAXRS.class);
    }

    public org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc create(org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc entity) {
        return webapi.create(entitypath,entity,"",false).getEntity();
    }

    public org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc create(org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc entity,String provider) {
        return webapi.create(entitypath,entity,provider,false).getEntity();
    }

    public org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc create(org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc entity,boolean broadcast) {
        return webapi.create(entitypath,entity,"",broadcast).getEntity();
    }

    public org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc read(long entityid,String provider) {
        return webapi.read(entitypath,entityid,provider).getEntity();
    }

    public org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc read(long entityid) {
        return webapi.read(entitypath,entityid,"").getEntity();
    }

    public List<org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc> readAll(String provider) {
        return webapi.readall(entitypath,provider).getEntityList();
    }

    public List<org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc> readAll() {
        return webapi.readall(entitypath,"").getEntityList();
    }

    public boolean update(long entityid,org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc update) {
        return webapi.update(entitypath,entityid,update,"").getRowsChanged() > 0;
    }

    public boolean update(long entityid,org.dieschnittstelle.esa.vertx.crud.testentities.StationaryTouchpointDoc update,String provider) {
        return webapi.update(entitypath,entityid,update,provider).getRowsChanged() > 0;
    }

    public boolean delete(long entityid) {
        return webapi.delete(entitypath,entityid,"").getRowsChanged() > 0;
    }

    public boolean delete(long entityid,String provider) {
        return webapi.delete(entitypath,entityid,provider).getRowsChanged() > 0;
    }


}
