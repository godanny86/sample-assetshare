package com.sample.assetshare.components.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

import com.sample.assetshare.components.TestModel;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {TestModel.class},
        resourceType = {TestModelImpl.RESOURCE_TYPE}
)
public class TestModelImpl implements TestModel {
    
    protected static final String RESOURCE_TYPE = "sample-assetshare/components/test";

    @Override
    public String getMessage() {
        return "Hello World";
    }

}
