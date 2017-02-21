package io.inbot.utils.maps;

import java.util.Map;

public class DelegatingRichMap<K,V> extends DelegatingMap<K, V> implements RichMap<K,V> {
    public DelegatingRichMap(Map<K,V> existing) {
        super(existing);
    }
}