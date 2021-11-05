/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.route53recoverycluster;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53recoverycluster.Route53RecoveryClusterClient;
import software.amazon.awssdk.services.route53recoverycluster.model.UpdateRoutingControlStateRequest;
import software.amazon.awssdk.services.route53recoverycluster.model.UpdateRoutingControlStateResponse;
import software.amazon.awssdk.services.route53recoverycontrolconfig.model.ClusterEndpoint;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateRoutingControlState {

    public static void main(String[] args) {
        final String USAGE = "\n" +
            "UpdateRoutingControlState - update the current state of the routing \n\n" +
            "Usage: UpdateRoutingControlState routingControlArn endpointsFile\n\n" +
            "Where:\n" +
            "  routingControlArn - Arn of the routing control.\n\n" +
            "  routingControlState - New state for the routing control.\n\n" +
            "  endpointsFile - path to the endpoints file.\n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String routingControlArn = args[0];
        String routingControlState = args[1];
        String endpointsFile = args[2];
        List<ClusterEndpoint> clusterEndpoints = getClusterEndpoints(endpointsFile);

        UpdateRoutingControlStateResponse response = updateRoutingControlState(clusterEndpoints, routingControlArn,
            routingControlState);
        System.out.println("UpdateRoutingControlStateResponse: " + response);
    }

    private static List<ClusterEndpoint> getClusterEndpoints(final String endpointsFile) {
        try {
            ClusterEndpoints endpoints =
                (new ObjectMapper().readValue(new File(endpointsFile), ClusterEndpoints.class));
            return Arrays.asList(endpoints.getClusterEndpoints()).stream().map(
                endpoint -> ClusterEndpoint.builder().endpoint(endpoint.getEndpoint()).region(endpoint.getRegion())
                    .build()).collect(
                Collectors.toList());
        } catch (IOException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public static UpdateRoutingControlStateResponse updateRoutingControlState(List<ClusterEndpoint> clusterEndpoints,
                                                                              String routingControlArn,
                                                                              String routingControlState) {
        for (ClusterEndpoint clusterEndpoint : clusterEndpoints) {
            try {
                System.out.println(clusterEndpoint);
                Route53RecoveryClusterClient client = Route53RecoveryClusterClient.builder()
                    .endpointOverride(URI.create(clusterEndpoint.endpoint()))
                    .region(Region.of(clusterEndpoint.region())).build();
                return client.updateRoutingControlState(
                    UpdateRoutingControlStateRequest.builder()
                        .routingControlArn(routingControlArn).routingControlState(routingControlState).build());
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
        return null;
    }
}

