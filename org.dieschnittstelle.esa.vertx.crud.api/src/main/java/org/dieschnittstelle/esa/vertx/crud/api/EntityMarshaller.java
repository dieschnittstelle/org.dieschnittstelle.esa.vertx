package org.dieschnittstelle.esa.vertx.crud.api;

import org.apache.log4j.Logger;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EntityMarshaller {

    protected static Logger logger = Logger.getLogger(EntityMarshaller.class);

    public interface Delegate {

        public Object createObject();

        public Object createArray();

        public boolean isArray(Object obj);

        public void add(Object arr,Object obj);

        public void put(Object obj,String attr,Object value);

    }

    private Delegate delegate;

    public EntityMarshaller(Delegate delegate) {
        this.delegate = delegate;
    }

    public Object marshal(String attr, Object value, Object parent) throws InvocationTargetException, IllegalAccessException {

        if (logger.isDebugEnabled()) {
            logger.debug("marshalling: " + value + " of class " + value.getClass());
        }

        if (parent == null) {
            parent = delegate.createObject();
            attr = "data";
        }

        // collections
        if (delegate.isArray(parent)) {
            if (isPrimitive(value)) {
                delegate.add(parent, value);
            } else if (isCollection(value)) {
                Object childarr = delegate.createArray();
                delegate.add(parent, childarr);
                for (Object member : (Collection) value) {
                    marshal(null, member, childarr);
                }
            } else {
                Object childobj = delegate.createObject();
                delegate.add(parent, childobj);
                marshalChildren(value,childobj);
            }
        }
        // objects
        else {
            if (isPrimitive(value)) {
                delegate.put(parent, attr, value);
            } else if (isCollection(value)) {
                Object childarr = delegate.createArray();
                delegate.put(parent, attr, childarr);
                for (Object member : (Collection) value) {
                    marshal(null, member, childarr);
                }
            } else {
                Object childobj = delegate.createObject();
                delegate.put(parent, attr, childobj);
                marshalChildren(value,childobj);
            }
        }

        return parent;
    }

    private void marshalChildren(Object value,Object parentContainer) throws InvocationTargetException, IllegalAccessException {
        // we use a simple approach determining the public getter methods and accessing field values via them
        for (Method meth : value.getClass().getMethods()) {
            if (hasMarshallableValue(meth)) {
                String fieldName = getFieldName(meth.getName());
                Object fieldValue = meth.invoke(value);
                if (fieldValue == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("marshal(): will not marshal null value for field " + fieldName);
                    }
                } else {
                    marshal(fieldName, fieldValue, parentContainer);
                }
            }
        }
    }

    private boolean isPrimitive(Object fieldValue) {
        return fieldValue instanceof Integer || fieldValue instanceof Long || fieldValue instanceof Float || fieldValue instanceof Double || fieldValue instanceof Boolean || fieldValue instanceof String;
    }

    private boolean isCollection(Object value) {
        return value instanceof Collection || value instanceof Array;
    }

    private boolean hasMarshallableValue(Method meth) {
        return !meth.getName().equals("getClass") && !Modifier.isStatic(meth.getModifiers()) && meth.getName().startsWith("get") && meth.getParameterTypes().length == 0;
    }


    private String getFieldName(String getter) {
        String field = getter.substring("get".length());
        return field.substring(0,1).toLowerCase() + field.substring(1);
    }





}
