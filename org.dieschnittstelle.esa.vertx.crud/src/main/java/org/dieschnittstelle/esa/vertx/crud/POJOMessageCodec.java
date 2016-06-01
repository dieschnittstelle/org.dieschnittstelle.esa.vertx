package org.dieschnittstelle.esa.vertx.crud;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by master on 27.05.16.
 */
public class POJOMessageCodec<T> implements MessageCodec<T,T> {

    protected static Logger logger = Logger.getLogger(POJOMessageCodec.class);

    private Class<T> pojoKlass;

    public POJOMessageCodec(Class<T> klass) {
        pojoKlass = klass;
    }

    @Override
    public void encodeToWire(Buffer buffer, T o) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
        logger.debug("encodeToWire()");
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            buffer.appendBytes(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public T decodeFromWire(int i, Buffer buffer) {
        logger.debug("decodeFromWire()");
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer.getBytes());
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
            return (T)ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public T transform(T o) {
        logger.debug("transform()");
        return (T)o;
    }

    @Override
    public String name() {
        return POJOMessageCodec.class.getName() + "4" + pojoKlass.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
