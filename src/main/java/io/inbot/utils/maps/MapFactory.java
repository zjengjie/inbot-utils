package io.inbot.utils.maps;

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
 * @param <V>
 *
 */
public class MapFactory {
    @SafeVarargs
    public static <K,V> Map<K,V> map(Entry<K, V>...entries) {
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
        return new ImmutableMapEntry<>(key,value);
    }

    public static <K,V> RichMap<K,V> richMap(Map<K,V> existing) {
        return new DelegatingRichMap<>(existing);
    }

    @SafeVarargs
    public static <K,V> RichMap<K,V> richMap(Map<K,V> existing, Entry<K, V>...entries) {
        DelegatingRichMap<K, V> newMap = new DelegatingRichMap<>(existing);
        newMap.put(entries);
        return newMap;
    }

    @SafeVarargs
    public static <K,V> RichMap<K,V> richMap(Entry<K, V>...entries) {
        DelegatingRichMap<K, V> newMap = new DelegatingRichMap<>(new HashMap<>());
        newMap.put(entries);
        return newMap;
    }

    public static <K,V> RichMultiMap<K, V> multiMap() {
        return new DelegatingRichMultiMap<>();
    }
}
