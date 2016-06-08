package org.dieschnittstelle.esa.webapi.client;

import com.google.gson.GsonBuilder;
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
        return webapi.create(entitypath,entity).getEntity();
    }

    public T read(long entityid) {
        return webapi.read(entitypath,entityid).getEntity();
    }

    public List<T> readAll() {
        return webapi.readall(entitypath).getEntityList();
    }

    public boolean update(long entityid,T update) {
        return webapi.update(entitypath,entityid,update).getRowsChanged() > 0;
    }

    public boolean delete(long entityid) {
        return webapi.delete(entitypath,entityid).getRowsChanged() > 0;
    }

}