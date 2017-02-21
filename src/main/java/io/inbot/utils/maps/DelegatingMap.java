package io.inbot.utils.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DelegatingMap<KEY,VALUE> implements Map<KEY,VALUE> {
    private final Map<KEY, VALUE> delegate;

    public DelegatingMap(Map<KEY,VALUE> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsKey(value);
    }

    @Override
    public VALUE get(Object key) {
        return delegate.get(key);
    }

    @Override
    public VALUE put(KEY key, VALUE value) {
        return delegate.put(key, value);
    }

    @Override
    public VALUE remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends KEY, ? extends VALUE> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<KEY> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<VALUE> values() {
        return delegate.values();
    }

    @Override
    public Set<java.util.Map.Entry<KEY, VALUE>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    public Map<KEY,VALUE> delegate() {
        return delegate;
    }
}