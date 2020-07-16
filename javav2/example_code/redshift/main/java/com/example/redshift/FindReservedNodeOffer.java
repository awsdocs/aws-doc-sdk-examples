//snippet-sourcedescription:[FindReservedNodeOffer.java demonstrates how to find additional Amazon Redshift nodes for purchase.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.redshift;

// snippet-start:[redshift.java2._nodes.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.DescribeReservedNodesResponse;
import software.amazon.awssdk.services.redshift.model.ReservedNode;
import software.amazon.awssdk.services.redshift.model.DescribeReservedNodeOfferingsRequest;
import software.amazon.awssdk.services.redshift.model.DescribeReservedNodeOfferingsResponse;
import software.amazon.awssdk.services.redshift.model.ReservedNodeOffering;
import software.amazon.awssdk.services.redshift.model.RedshiftException ;
import java.util.ArrayList;
// snippet-end:[redshift.java2._nodes.import]

public class FindReservedNodeOffer {

    public static String nodeTypeToPurchase = "dc2.large";
    public static Double fixedPriceLimit = 10000.00;
    public static ArrayList<ReservedNodeOffering> matchingNodes = new ArrayList<ReservedNodeOffering>();

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
                .region(region)
                .build();

        listReservedNodes(redshiftClient);
        findReservedNodeOffer(redshiftClient);
    }

    // snippet-start:[redshift.java2._nodes.main]
    public static void listReservedNodes(RedshiftClient redshiftClient) {

        try {

            DescribeReservedNodesResponse reservedNodesResponse = redshiftClient.describeReservedNodes();
            System.out.println("Listing nodes already purchased.");

            for (ReservedNode node : reservedNodesResponse.reservedNodes()) {
                printReservedNodeDetails(node);
            }

        } catch (RedshiftException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void findReservedNodeOffer(RedshiftClient redshiftClient) {
     try {
        DescribeReservedNodeOfferingsRequest request = DescribeReservedNodeOfferingsRequest.builder()
                .build();

        DescribeReservedNodeOfferingsResponse response = redshiftClient.describeReservedNodeOfferings(request);
        Integer count = 0;
        System.out.println("\nFinding nodes to purchase.");

        for (ReservedNodeOffering offering : response.reservedNodeOfferings())
        {
            if (offering.nodeType().equals(nodeTypeToPurchase)){

                if (offering.fixedPrice() < fixedPriceLimit) {
                    matchingNodes.add(offering);
                    printOfferingDetails(offering);
                    count +=1;
                }
            }
        }
        if (count == 0) {
            System.out.println("\nNo reserved node offering matches found.");
        } else {
            System.out.println("\nFound " + count + " matches.");
        }

     } catch (RedshiftException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
    }

    private static void printReservedNodeDetails(ReservedNode node) {
        System.out.println("\nPurchased Node Details:");
        System.out.format("Id: %s\n", node.reservedNodeOfferingId());
        System.out.format("State: %s\n", node.state());
        System.out.format("Node Type: %s\n", node.nodeType());
        System.out.format("Start Time: %s\n", node.startTime());
        System.out.format("Fixed Price: %s\n", node.fixedPrice());
        System.out.format("Offering Type: %s\n", node.offeringType());
        System.out.format("Duration: %s\n", node.duration());
    }

    private static void printOfferingDetails(
            ReservedNodeOffering offering) {
        System.out.println("\nOffering Match:");
        System.out.format("Id: %s\n", offering.reservedNodeOfferingId());
        System.out.format("Node Type: %s\n", offering.nodeType());
        System.out.format("Fixed Price: %s\n", offering.fixedPrice());
        System.out.format("Offering Type: %s\n", offering.offeringType());
        System.out.format("Duration: %s\n", offering.duration());
    }
    // snippet-end:[redshift.java2._nodes.main]
}
