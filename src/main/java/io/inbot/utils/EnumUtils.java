package io.inbot.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class EnumUtils {

    public static <T extends Enum<?>> Optional<T> getEnumValue(Class<T> clazz, String value) {
        T[] enumConstants=clazz.getEnumConstants();
        for(T ec: enumConstants) {
            if(ec.name().equals(value)) {
                return Optional.of(ec);
            }
        }

        return Optional.empty();
    }

    public static <T extends Enum<?>> Optional<T> getEnumValueIgnoreCase(Class<T> clazz, String value) {
        T[] enumConstants=clazz.getEnumConstants();
        for(T ec: enumConstants) {
            if(ec.name().equalsIgnoreCase(value)) {
                return Optional.of(ec);
            }
        }

        return Optional.empty();
    }

    public static <T extends Enum<?>> Stream<T> stream(Class<T> clazz) {
        return Arrays.stream(clazz.getEnumConstants());
    }
}
