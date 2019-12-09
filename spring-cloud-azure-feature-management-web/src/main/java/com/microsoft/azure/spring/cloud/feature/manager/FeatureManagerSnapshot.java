/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.cloud.feature.manager;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Holds information on Feature Management properties and can check if a given feature is
 * enabled. Returns the same value in the same request.
 */
@Configuration
public class FeatureManagerSnapshot {

    private FeatureManager featureManager;

    private HashMap<String, Boolean> requestMap;

    public FeatureManagerSnapshot(FeatureManager featureManager) {
        this.featureManager = featureManager;
        this.requestMap = new HashMap<String, Boolean>();
    }

    /**
     * Checks to see if the feature is enabled. If enabled it check each filter, once a
     * single filter returns true it returns true. If no filter returns true, it returns
     * false. If there are no filters, it returns true. If feature isn't found it returns
     * false.
     * 
     * If isEnabled has already been called on this feature in this request, it will
     * return the same value as it did before.
     * 
     * @param feature Feature being checked.
     * @return state of the feature
     */
    public Future<Boolean> isEnabledAsync(String feature) {
        if (requestMap.get(feature) != null) {
            return CompletableFuture.supplyAsync(() -> (boolean) requestMap.get(feature));
        }

        return ((CompletableFuture<Boolean>) featureManager.isEnabledAsync(feature))
                .whenComplete((enabled, exception) -> requestMap.put(feature, enabled));
    }
}
