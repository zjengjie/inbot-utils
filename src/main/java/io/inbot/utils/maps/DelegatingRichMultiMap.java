package io.inbot.utils.maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;

public class DelegatingRichMultiMap<K,V> extends DelegatingMap<K, Collection<V>> implements RichMultiMap<K,V> {
    private final Supplier<Collection<V>> supplier;

    public DelegatingRichMultiMap() {
        this(new HashMap<>(),() -> new LinkedHashSet<V>());
    }

    public DelegatingRichMultiMap(Map<K,Collection<V>> existing) {
        this(existing,() -> new LinkedHashSet<>());
    }

    public DelegatingRichMultiMap(Map<K,Collection<V>> existing, Supplier<Collection<V>> supplier) {
        super(existing);
        this.supplier = supplier;
    }

    @Override
    public Supplier<Collection<V>> supplier() {
        return supplier;
    }
}