package com.github.hteph.utils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;


public class StreamUtilities {

    public static <T> Stream<T> getStreamEmptyIfNull(Collection<T> collection) {
        return Optional.ofNullable(collection).stream().flatMap(Collection::stream);
    }
}
