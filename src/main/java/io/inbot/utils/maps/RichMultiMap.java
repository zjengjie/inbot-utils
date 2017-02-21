package io.inbot.utils.maps;

import java.util.Collection;
import java.util.function.Supplier;

public interface RichMultiMap<K, V> extends RichMap<K,Collection<V>> {
    Supplier<Collection<V>> supplier();

    default Collection<V> putValue(K key, V value) {
        Collection<V> collection = getOrCreate(key, supplier());
        collection.add(value);
        return collection;
    }

    @SuppressWarnings("unchecked")
    default void putValue(Entry<K,V>...entries) {
        for(Entry<K, V> e: entries) {
            putValue(e.getKey(),e.getValue());
        }
    }
}