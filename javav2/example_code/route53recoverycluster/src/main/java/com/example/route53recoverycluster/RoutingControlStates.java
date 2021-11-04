package com.example.myapp;

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
