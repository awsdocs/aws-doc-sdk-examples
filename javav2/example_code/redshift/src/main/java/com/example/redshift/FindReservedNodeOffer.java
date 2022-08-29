//snippet-sourcedescription:[FindReservedNodeOffer.java demonstrates how to find additional Amazon Redshift nodes for purchase.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Redshift]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshift;

// snippet-start:[redshift.java2._nodes.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class FindReservedNodeOffer {

    public static String nodeTypeToPurchase = "dc2.large";
    public static Double fixedPriceLimit = 10000.00;
    public static ArrayList<ReservedNodeOffering> matchingNodes = new ArrayList<>();

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listReservedNodes(redshiftClient);
        findReservedNodeOffer(redshiftClient);
        redshiftClient.close();
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
            int count = 0;
            System.out.println("\nFinding nodes to purchase.");

            for (ReservedNodeOffering offering : response.reservedNodeOfferings()) {
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

    private static void printOfferingDetails(ReservedNodeOffering offering) {
        System.out.println("\nOffering Match:");
        System.out.format("Id: %s\n", offering.reservedNodeOfferingId());
        System.out.format("Node Type: %s\n", offering.nodeType());
        System.out.format("Fixed Price: %s\n", offering.fixedPrice());
        System.out.format("Offering Type: %s\n", offering.offeringType());
        System.out.format("Duration: %s\n", offering.duration());
    }
    // snippet-end:[redshift.java2._nodes.main]
}
