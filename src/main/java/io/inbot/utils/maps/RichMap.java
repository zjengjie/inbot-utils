package io.inbot.utils.maps;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface RichMap<K,V> extends Map<K,V> {
    default void put(@SuppressWarnings("unchecked") Entry<K,V>...es) {
        for(java.util.Map.Entry<K, V> e: es) {
            put(e.getKey(),e.getValue());
        }
    }

    default Optional<V> maybeGet(K key) {
        return Optional.ofNullable(get(key));
    }

    default boolean put(K key, Optional<V> value) {
        if(value.isPresent()) {
            put(key,value.get());
            return true;
        } else {
            return false;
        }
    }

    default V getOrCreate(K key, Supplier<V> supplier) {
        Optional<V> maybeExists = maybeGet(key);
        if(maybeExists.isPresent()) {
            return maybeExists.get();
        } else {
            V newValue = supplier.get();
            put(key,newValue);
            return newValue;
        }
    }
}