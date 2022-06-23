/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.route53recoverycluster;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoutingControlState {
    @JsonProperty("RoutingControlArn")
    private String routingControlArn;
    @JsonProperty("RoutingControlState")
    private String routingControlState;

    public String getRoutingControlArn() {
        return routingControlArn;
    }

    public void setRoutingControlArn(final String routingControlArn) {
        this.routingControlArn = routingControlArn;
    }

    public String getRoutingControlState() {
        return routingControlState;
    }

    public void setRoutingControlState(final String routingControlState) {
        this.routingControlState = routingControlState;
    }
}