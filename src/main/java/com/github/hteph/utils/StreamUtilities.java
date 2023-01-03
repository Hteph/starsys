package com.github.hteph.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtilities {

    public static <T> Stream<T> getStreamEmptyIfNull(Collection<T> collection) {
        return Optional.ofNullable(collection).stream().flatMap(Collection::stream);
    }

    public static <K, V extends Comparable<V>> Optional<V> findMaxInMap(Map<K, V> map) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getValue);
        
    }
}
