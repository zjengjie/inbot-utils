package io.inbot.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.MDC;

public class MdcContext implements AutoCloseable {
    private final List<String> keys = new ArrayList<>();

    private MdcContext() {
    }

    public void put(String key, Object value) {
        if(key.contains(".")) {
            throw new IllegalArgumentException("no dots allowed in mdc keys");
        }
        if(value !=null) {
            keys.add(key);
            MDC.put(key, value.toString());
        }
    }

    @Override
    public void close() {
        for(String key:keys) {
            MDC.remove(key);
        }
    }

    public static MdcContext create() {
        return new MdcContext();
    }
}
