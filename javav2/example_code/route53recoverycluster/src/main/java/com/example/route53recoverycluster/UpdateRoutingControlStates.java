/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.route53recoverycluster;

//snippet-start:[route53_rec.java2.update_routing_states.import]
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53recoverycluster.Route53RecoveryClusterClient;
import software.amazon.awssdk.services.route53recoverycluster.model.UpdateRoutingControlStateEntry;
import software.amazon.awssdk.services.route53recoverycluster.model.UpdateRoutingControlStatesRequest;
import software.amazon.awssdk.services.route53recoverycluster.model.UpdateRoutingControlStatesResponse;
import software.amazon.awssdk.services.route53recoverycontrolconfig.model.ClusterEndpoint;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
//snippet-end:[route53_rec.java2.update_routing_states.import]

import static java.util.Arrays.asList;

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateRoutingControlStates {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "UpdateRoutingControlState - update the current state of the routing \n\n" +
                "Usage: UpdateRoutingControlState routingControlArn endpointsFile\n\n" +
                "Where:\n" +
                "  routingControlStatesFile - routing control updates entries file\n\n" +
                "  endpointsFile - path to the endpoints file.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String routingControlStatesFile = args[0];
        String endpointsFile = args[1];
        List<UpdateRoutingControlStateEntry> routingControlStateEntries =
                getUpdateRoutingControlEntries(routingControlStatesFile);
        List<ClusterEndpoint> clusterEndpoints = getClusterEndpoints(endpointsFile);
        UpdateRoutingControlStatesResponse response = updateRoutingControlStates(clusterEndpoints,
                routingControlStateEntries);
        System.out.println("UpdateRoutingControlStatesResponse: " + response);
    }

    //snippet-start:[route53_rec.java2.update_routing_states.main]
    private static List<UpdateRoutingControlStateEntry> getUpdateRoutingControlEntries(
            final String routingControlStatesFile) {
        try {
            final RoutingControlStates routingControlStates =
                    new ObjectMapper().readValue(new File(routingControlStatesFile), RoutingControlStates.class);
            return Arrays.asList(routingControlStates.getRoutingControlStates()).stream().map(
                    routingControlState -> UpdateRoutingControlStateEntry.builder()
                            .routingControlArn(routingControlState.getRoutingControlArn())
                            .routingControlState(routingControlState.getRoutingControlState()).build()).collect(
                    Collectors.toList());
        } catch (IOException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    private static List<ClusterEndpoint> getClusterEndpoints(final String endpointsFile) {
        try {
            ClusterEndpoints endpoints =
                    (new ObjectMapper().readValue(new File(endpointsFile), ClusterEndpoints.class));
            return asList(endpoints.getClusterEndpoints()).stream().map(
                    endpoint -> ClusterEndpoint.builder().endpoint(endpoint.getEndpoint()).region(endpoint.getRegion())
                            .build()).collect(
                    Collectors.toList());
        } catch (IOException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public static UpdateRoutingControlStatesResponse updateRoutingControlStates(List<ClusterEndpoint> clusterEndpoints,
                                                                                Collection<UpdateRoutingControlStateEntry> updateRoutingControlStateEntries) {
        for (ClusterEndpoint clusterEndpoint : clusterEndpoints) {
            try {
                System.out.println(clusterEndpoint);
                Route53RecoveryClusterClient client = Route53RecoveryClusterClient.builder()
                        .endpointOverride(URI.create(clusterEndpoint.endpoint()))
                        .region(Region.of(clusterEndpoint.region())).build();
                return client.updateRoutingControlStates(UpdateRoutingControlStatesRequest.builder()
                        .updateRoutingControlStateEntries(updateRoutingControlStateEntries).build());
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
        return null;
    }
    //snippet-end:[route53_rec.java2.update_routing_states.main]
}