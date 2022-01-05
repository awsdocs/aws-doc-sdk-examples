/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.route53recoverycluster;

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
