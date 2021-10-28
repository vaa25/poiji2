package com.poiji.bind;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by hakan on 08/03/2018
 */
public interface Unmarshaller {

    <T> void unmarshal(Class<T> type, Consumer<? super T> consumer);

    <T> Stream<T> stream(Class<T> type);
}
