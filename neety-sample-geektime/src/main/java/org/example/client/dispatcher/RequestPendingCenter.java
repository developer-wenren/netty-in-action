package org.example.client.dispatcher;

import io.netty.example.study.common.OperationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author one
 * @date 2020/04/12
 */
public class RequestPendingCenter {
    private Map<Long, OperationResultFeature> maps = new ConcurrentHashMap<>(256);

    public void add(Long id, OperationResultFeature feature) {
        maps.put(id, feature);
    }

    public void set(Long id, OperationResult result) {
        OperationResultFeature operationResultFeature = maps.get(id);
        if (operationResultFeature != null) {
            operationResultFeature.setSuccess(result);
            maps.remove(id);
        }
    }
}
