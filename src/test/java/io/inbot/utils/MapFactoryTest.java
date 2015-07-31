package io.inbot.utils;

import static io.inbot.utils.MapFactory.entry;
import static io.inbot.utils.MapFactory.hashMap;
import static io.inbot.utils.MapFactory.map;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.annotations.Test;

@Test
public class MapFactoryTest {

    public void shouldCreateMap() {
        Map<String,Integer> map = hashMap(entry("one", 1), entry("two",2), entry("three",3));
        assertThat(map.get("one")).isEqualTo(1);
        assertThat(map.get("two")).isEqualTo(2);
        assertThat(map.get("three")).isEqualTo(3);
    }

    public void shouldCreateConcurrentHashMap() {
        Map<String,Integer> map = map(() -> new ConcurrentHashMap<>(), entry("one", 1));
        assertThat(map.get("one")).isEqualTo(1);
    }
}
