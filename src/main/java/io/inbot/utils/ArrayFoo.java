package io.inbot.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class ArrayFoo {
    @SafeVarargs
    public static <T> T[] combine(T[]...arrays) {
        Validate.isTrue(arrays.length > 0, "should have at least one element");
        if(arrays.length == 1) {
            return arrays[0];
        }
        ArrayList<T> list = new ArrayList<T>(1000);
        for(T[] ts: arrays) {
            for(T t:ts) {
                list.add(t);
            }
        }
        return list.toArray(arrays[0]);
    }


    @SafeVarargs
    public static <T> Set<T> setOf(T...ts) {
        Set<T> set = new LinkedHashSet<>();
        for(T t:ts) {
            set.add(t);
        }
        return Collections.unmodifiableSet(set);
    }

    public static <T> String stringify(T[] ts) {
        return "["+ StringUtils.join(ts,',') +"]";
    }

    @SafeVarargs
    public static <T> T[] array(T...ts) {
        return ts;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] array(Collection<T> ts) {
        Validate.isTrue(ts.size()>0,"should have at least one element");
        // nice little hack to use reflection to create the array and dodge the ClassCastException
        return ts.toArray((T[]) Array.newInstance(ts.iterator().next().getClass(), 0));
    }
}
