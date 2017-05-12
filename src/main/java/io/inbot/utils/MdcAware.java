package io.inbot.utils;

import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.MDC;

public class MdcAware {
    public static Runnable wrap(Runnable task)  {
        final Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        return () -> {
            // inherit parent context to whatever thread this executes on
            MDC.setContextMap(mdcMap);
            task.run();
            // return thread MDC to virgin state
            MDC.clear();
        };
    }

    public static <T> Callable<T> wrap(Callable<T> task)  {
        final Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        return () -> {
            // inherit parent context to whatever thread this executes on
            MDC.setContextMap(mdcMap);
            T result = task.call();
            // return thread MDC to virgin state
            MDC.clear();
            return result;
        };
    }
}
