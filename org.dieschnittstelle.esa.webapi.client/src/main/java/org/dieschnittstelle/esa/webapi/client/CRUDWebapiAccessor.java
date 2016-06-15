package org.dieschnittstelle.esa.webapi.client;

import com.google.gson.GsonBuilder;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;
import org.dieschnittstelle.esa.webapi.client.gson.CRUDResultGsonDeserialiser;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by master on 01.06.16.
 */
public class CRUDWebapiAccessor<T> {

    private String entitypath;
    private Class<T> entityclass;
    private String baseUrl;
    private CRUDWebapi<T> webapi;
    private GsonConverter converter;

    public CRUDWebapiAccessor(String entitypath, Class<T> entityclass, String baseUrl) {
        this.entitypath = entitypath;
        this.entityclass = entityclass;
        this.baseUrl = baseUrl;

        this.converter = new GsonConverter(new GsonBuilder()
                .registerTypeAdapter(CRUDResult.class, new CRUDResultGsonDeserialiser<T>(entityclass)).create());

        RestAdapter adapter = new retrofit.RestAdapter.Builder().setEndpoint(baseUrl).setConverter(converter).build();
        webapi = adapter.create(CRUDWebapi.class);
    }

    public T create(T entity) {
        return webapi.create(entitypath,entity,"",false).getEntity();
    }

    public T create(T entity,String provider) {
        return webapi.create(entitypath,entity,provider,false).getEntity();
    }

    public T create(T entity,boolean broadcast) {
        return webapi.create(entitypath,entity,"",broadcast).getEntity();
    }

    public T read(long entityid,String provider) {
        return webapi.read(entitypath,entityid,provider).getEntity();
    }

    public T read(long entityid) {
        return webapi.read(entitypath,entityid,"").getEntity();
    }

    public List<T> readAll(String provider) {
        return webapi.readall(entitypath,provider).getEntityList();
    }

    public List<T> readAll() {
        return webapi.readall(entitypath,"").getEntityList();
    }

    public boolean update(long entityid,T update) {
        return webapi.update(entitypath,entityid,update,"").getRowsChanged() > 0;
    }

    public boolean update(long entityid,T update,String provider) {
        return webapi.update(entitypath,entityid,update,provider).getRowsChanged() > 0;
    }

    public boolean delete(long entityid) {
        return webapi.delete(entitypath,entityid,"").getRowsChanged() > 0;
    }

    public boolean delete(long entityid,String provider) {
        return webapi.delete(entitypath,entityid,provider).getRowsChanged() > 0;
    }


}
