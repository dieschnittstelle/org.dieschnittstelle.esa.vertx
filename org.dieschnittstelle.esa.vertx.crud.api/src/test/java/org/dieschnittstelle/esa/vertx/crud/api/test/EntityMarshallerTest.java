package org.dieschnittstelle.esa.vertx.crud.api.test;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;
import org.dieschnittstelle.esa.vertx.crud.api.EntityMarshaller;
import org.dieschnittstelle.jee.esa.entities.crm.Address;
import org.dieschnittstelle.jee.esa.entities.crm.MobileTouchpoint;
import org.dieschnittstelle.jee.esa.entities.crm.StationaryTouchpoint;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.text.html.parser.Entity;

/**
 * Created by master on 07.06.16.
 */
public class EntityMarshallerTest {

    protected static Logger logger = Logger.getLogger(EntityMarshallerTest.class);

    private EntityMarshaller.Delegate delegate = new EntityMarshaller.Delegate() {

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
    };

    private EntityMarshaller marshaller = new EntityMarshaller(delegate);

    @Test
    public void testObjWithChildObj() {

        StationaryTouchpoint tp = new StationaryTouchpoint(-1,"dorem",new Address("lipsum","olor","-42","adispiscing"));
        JsonObject wrapper = new JsonObject();
        try {
            JsonObject marshalled = (JsonObject)marshaller.marshal("data", tp, wrapper);
            logger.info("marshalled: " + marshalled);
            StationaryTouchpoint unmarshalled = Json.decodeValue(((JsonObject)marshalled.getJsonObject("data")).encode(),StationaryTouchpoint.class);
            logger.info("unmarshalled: " + unmarshalled);

            assertEquals(tp.getName(),unmarshalled.getName());
            assertEquals(tp.getAddress().getStreet(),unmarshalled.getAddress().getStreet());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testObjWithSimpleChildArray() {
        MobileTouchpoint tp = new MobileTouchpoint();
        tp.addMobilePhoneId("000-000");
        tp.addMobilePhoneId("111-111");
        tp.setName("lirum");
        JsonObject wrapper = new JsonObject();
        try {
            JsonObject marshalled = (JsonObject)marshaller.marshal("data", tp, wrapper);
            logger.info("marshalled: " + marshalled);
            MobileTouchpoint unmarshalled = Json.decodeValue(((JsonObject)marshalled.getJsonObject("data")).encode(),MobileTouchpoint.class);
            logger.info("unmarshalled: " + unmarshalled);

            assertEquals(tp.getName(),unmarshalled.getName());
            assertTrue(unmarshalled.getMobilePhoneIds().contains("000-000") && unmarshalled.getMobilePhoneIds().contains("111-111"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
