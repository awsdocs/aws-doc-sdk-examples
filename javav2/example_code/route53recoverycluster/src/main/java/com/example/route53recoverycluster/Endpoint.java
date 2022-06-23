/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.route53recoverycluster;

import com.fasterxml.jackson.annotation.JsonProperty;

class Endpoint {
    @JsonProperty("Endpoint")
    private String endpoint;

    @JsonProperty("Region")
    private String region;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }
}