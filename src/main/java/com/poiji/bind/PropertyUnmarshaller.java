package com.poiji.bind;

/**
 * Created by hakan on 25.05.2020
 */
public interface PropertyUnmarshaller {

    <T> T unmarshal(Class<T> type);

}
