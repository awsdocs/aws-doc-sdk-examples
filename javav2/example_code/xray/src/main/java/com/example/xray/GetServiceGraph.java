//snippet-sourcedescription:[GetServiceGraph.java demonstrates how to describe services that process incoming requests.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS X-Ray Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[09/29/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.xray;

// snippet-start:[xray.java2_get_graph.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetServiceGraphRequest;
import software.amazon.awssdk.services.xray.model.GetServiceGraphResponse;
import software.amazon.awssdk.services.xray.model.Service;
import software.amazon.awssdk.services.xray.model.XRayException;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
// snippet-end:[xray.java2_get_graph.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetServiceGraph {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "   <groupName>\n\n" +
                "Where:\n" +
                "   groupName - the name of a group based on which you want to generate a graph.\n\n";

         if (args.length != 1) {
             System.out.println(USAGE);
              System.exit(1);
         }

        String groupName = args[0];
        Region region = Region.US_EAST_1;
        XRayClient xRayClient = XRayClient.builder()
                .region(region)
                .build();

        getGraph(xRayClient, groupName);
    }

    // snippet-start:[xray.java2_get_graph.main]
    public static void getGraph(XRayClient xRayClient, String groupName){

        try{
            // The Instant values have to be 6 hours apart.
            LocalDateTime localDateTime = LocalDateTime.parse("2021-09-09T06:00:00");
            Instant start = localDateTime.atZone(ZoneId.of("America/New_York")).toInstant();

            LocalDateTime localDateTime2 = LocalDateTime.parse("2021-09-09T12:00:00");
            Instant end = localDateTime2.atZone(ZoneId.of("America/New_York")).toInstant();

            GetServiceGraphRequest getServiceGraphRequest = GetServiceGraphRequest.builder()
                .groupName(groupName)
                .startTime(start)
                .endTime(end)
                .build();

            GetServiceGraphResponse graphResponse = xRayClient.getServiceGraph(getServiceGraphRequest);
            List<Service> services = graphResponse.services();

            for (Service service: services) {
                System.out.println("The name of the service is  "+service.name());
            }

        } catch (XRayException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[xray.java2_get_graph.main]
}
