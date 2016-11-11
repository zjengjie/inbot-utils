package io.inbot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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

    public static void withContext(Consumer<MdcContext> block) {
        try(MdcContext ctx=MdcContext.create()) {
            block.accept(ctx);
        }
    }

    public static MdcContext create() {
        return new MdcContext();
    }
}
