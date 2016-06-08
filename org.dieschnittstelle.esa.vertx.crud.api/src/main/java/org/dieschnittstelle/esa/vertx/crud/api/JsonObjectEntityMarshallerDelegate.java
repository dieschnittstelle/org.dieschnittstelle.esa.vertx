package org.dieschnittstelle.esa.vertx.crud.api;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by master on 08.06.16.
 */
public class JsonObjectEntityMarshallerDelegate implements EntityMarshaller.Delegate {
    @Override
    public Object createObject() {
        return new JsonObject();
    }

    @Override
    public Object createArray() {
        return new JsonArray();
    }

    @Override
    public boolean isArray(Object obj) {
        return obj instanceof JsonArray;
    }

    @Override
    public void add(Object arr, Object obj) {
        ((JsonArray)arr).add(obj);
    }

    @Override
    public void put(Object obj, String attr, Object value) {
        ((JsonObject)obj).put(attr,value);
    }

}
