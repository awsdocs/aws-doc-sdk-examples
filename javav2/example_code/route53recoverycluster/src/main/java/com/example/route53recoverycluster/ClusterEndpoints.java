package com.example.myapp;

import com.fasterxml.jackson.annotation.JsonProperty;

class ClusterEndpoints {
    @JsonProperty("Endpoints")
    private Endpoint[] clusterEndpoints;

    public Endpoint[] getClusterEndpoints() {
        return clusterEndpoints;
    }

    public void setClusterEndpoints(final Endpoint[] clusterEndpoints) {
        this.clusterEndpoints = clusterEndpoints;
    }
}
