package com.example.myapp;

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
