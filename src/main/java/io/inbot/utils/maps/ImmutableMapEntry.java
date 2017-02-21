package io.inbot.utils.maps;

import java.util.Map.Entry;

public class ImmutableMapEntry<KEY, VALUE> implements Entry<KEY,VALUE> {
    private final KEY key;
    private final VALUE value;

    public ImmutableMapEntry(KEY key, VALUE value) {
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