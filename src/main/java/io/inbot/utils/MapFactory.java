package io.inbot.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Nice simple factory for creating maps quickly. Add hasMap and entry as static imports and you can write stuff like
 *
 * Map&lt;String,Integer&gt; map = hashMap(entry(&quot;one&quot;,1),entry(&quot;two&quot;,2));
 *
 * Use map with a supplier to create maps of any type.
 *
 */
public class MapFactory {
    @SafeVarargs
    public static <K,V> Map<K,V> hashMap(Entry<K, V>...entries) {
        return map(() -> new HashMap<>(), entries);
    }

    @SafeVarargs
    public static <K,V> Map<K,V> map(Supplier<Map<K,V>> mapFactory, Entry<K, V>...entries) {
        Map<K, V> newMap = mapFactory.get();
        for(Entry<K,V> entry: entries) {
            newMap.put(entry.getKey(), entry.getValue());
        }
        return newMap;
    }

    public static <K,V> Entry<K,V> entry(K key, V value) {
        return new SimpleEntry<>(key,value);
    }

    public static class SimpleEntry<KEY, VALUE> implements Entry<KEY,VALUE> {
        private final KEY key;
        private final VALUE value;

        public SimpleEntry(KEY key, VALUE value) {
            this.key=key;
            this.value=value;
        }

        @Override
        public KEY getKey() {
            return key;
        }

        @Override
        public VALUE getValue() {
            return value;
        }

        @Override
        public VALUE setValue(VALUE value) {
            throw new UnsupportedOperationException("entries are immutable");
        }
    }
}
