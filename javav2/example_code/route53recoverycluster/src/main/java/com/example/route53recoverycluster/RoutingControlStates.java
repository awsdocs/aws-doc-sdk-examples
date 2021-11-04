/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.route53recoverycluster;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoutingControlStates {
    @JsonProperty("RoutingControlStates")
    private RoutingControlState[] routingControlStates;

    public RoutingControlState[] getRoutingControlStates() {
        return routingControlStates;
    }

    public void setRoutingControlStates(final RoutingControlState[] routingControlStates) {
        this.routingControlStates = routingControlStates;
    }
}
