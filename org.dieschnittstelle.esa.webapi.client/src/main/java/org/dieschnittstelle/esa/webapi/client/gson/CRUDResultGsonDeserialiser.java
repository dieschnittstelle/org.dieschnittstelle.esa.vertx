package org.dieschnittstelle.esa.webapi.client.gson;

import com.google.gson.*;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.CRUDResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by master on 02.06.16.
 */
public class CRUDResultGsonDeserialiser<T> implements JsonDeserializer<CRUDResult> {
    private Gson gson;
    private Class<T> entityklass;

    protected static Logger logger = Logger.getLogger(CRUDResultGsonDeserialiser.class);

    public CRUDResultGsonDeserialiser(Class<T> klass) {
        this.gson = new Gson();
        this.entityklass = klass;
    }

    @Override
    public CRUDResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        logger.info("deserialise(): " + jsonElement);
        //T deserialised = gson.fromJson(jsonElement,entityklass);
        CRUDResult<T> result = new CRUDResult<T>();

        JsonObject root = jsonElement.getAsJsonObject();
        JsonElement attr;

        attr = (JsonObject) root.get("entity");
        if (attr != null) {
            logger.info("deserialise(): serialising entity...");
            result.setEntity(gson.fromJson(attr.getAsJsonObject(),this.entityklass));
        }
        else {
            attr = root.get("entityList");
            if (attr != null) {
                List<T> entities = new ArrayList<T>();
                logger.info("deserialise(): serialising entitylist...");
                for (JsonElement cobj : attr.getAsJsonArray()) {
                    entities.add(gson.fromJson((JsonObject) cobj.getAsJsonObject(), this.entityklass));
                }
                result.setEntityList(entities);
            }
            else {
                attr = root.get("rowsChanged");
                if (attr != null)
                    logger.info("deserialise(): setting rowsChanged...");
                    result.setRowsChanged(attr.getAsInt());
            }
        }

        attr = root.get("entityId");
        if (attr != null) {
            result.setEntityId(attr.getAsLong());
        }

        return result;
    }

}
