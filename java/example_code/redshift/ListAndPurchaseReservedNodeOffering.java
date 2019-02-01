/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[ListAndPurchaseReservedNodeOffering demonstrates how to list and purchase Amazon Redshift reserved node offerings.]
// snippet-service:[redshift]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Redshift]
// snippet-keyword:[Code Sample]
// snippet-keyword:[DescribeReservedNodeOfferings]
// snippet-keyword:[PurchaseReservedNodeOffering]
// snippet-keyword:[ReservedNode]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[redshift.java.ListAndPurchaseReservedNodeOffering.complete]

package com.amazonaws.services.redshift;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.amazonaws.services.redshift.model.*;

public class ListAndPurchaseReservedNodeOffering {

    public static AmazonRedshift client;
    public static String nodeTypeToPurchase = "dc2.large";
    public static Double fixedPriceLimit = 10000.00;
    public static ArrayList<ReservedNodeOffering> matchingNodes = new ArrayList<ReservedNodeOffering>();

    public static void main(String[] args) throws IOException {

        // Default client using the {@link com.amazonaws.auth.DefaultAWSCredentialsProviderChain}
       client = AmazonRedshiftClientBuilder.defaultClient();

        try {
             listReservedNodes();
             findReservedNodeOffer();
             purchaseReservedNodeOffer();

        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static void listReservedNodes() {
        DescribeReservedNodesResult result = client.describeReservedNodes();
        System.out.println("Listing nodes already purchased.");
        for (ReservedNode node : result.getReservedNodes()) {
            printReservedNodeDetails(node);
        }
    }

    private static void findReservedNodeOffer()
    {
        DescribeReservedNodeOfferingsRequest request = new DescribeReservedNodeOfferingsRequest();
        DescribeReservedNodeOfferingsResult result = client.describeReservedNodeOfferings(request);
        Integer count = 0;

        System.out.println("\nFinding nodes to purchase.");
        for (ReservedNodeOffering offering : result.getReservedNodeOfferings())
        {
            if (offering.getNodeType().equals(nodeTypeToPurchase)){

                if (offering.getFixedPrice() < fixedPriceLimit) {
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
    }

    private static void purchaseReservedNodeOffer() throws IOException {
        if (matchingNodes.size() == 0) {
            return;
        } else {
            System.out.println("\nPurchasing nodes.");

            for (ReservedNodeOffering offering : matchingNodes) {
                printOfferingDetails(offering);
                System.out.println("WARNING: purchasing this offering will incur costs.");
                System.out.println("Purchase this offering [Y or N]?");
                DataInput in = new DataInputStream(System.in);
                String purchaseOpt = in.readLine();
                if (purchaseOpt.equalsIgnoreCase("y")){

                    try {
                        PurchaseReservedNodeOfferingRequest request = new PurchaseReservedNodeOfferingRequest()
                            .withReservedNodeOfferingId(offering.getReservedNodeOfferingId());
                        ReservedNode reservedNode = client.purchaseReservedNodeOffering(request);
                        printReservedNodeDetails(reservedNode);
                    }
                    catch (ReservedNodeAlreadyExistsException ex1){
                    }
                    catch (ReservedNodeOfferingNotFoundException ex2){
                    }
                    catch (ReservedNodeQuotaExceededException ex3){
                    }
                    catch (Exception ex4){
                    }
                }
            }
            System.out.println("Finished.");

        }
    }

    private static void printOfferingDetails(
            ReservedNodeOffering offering) {
        System.out.println("\nOffering Match:");
        System.out.format("Id: %s\n", offering.getReservedNodeOfferingId());
        System.out.format("Node Type: %s\n", offering.getNodeType());
        System.out.format("Fixed Price: %s\n", offering.getFixedPrice());
        System.out.format("Offering Type: %s\n", offering.getOfferingType());
        System.out.format("Duration: %s\n", offering.getDuration());
    }

    private static void printReservedNodeDetails(ReservedNode node) {
        System.out.println("\nPurchased Node Details:");
        System.out.format("Id: %s\n", node.getReservedNodeOfferingId());
        System.out.format("State: %s\n", node.getState());
        System.out.format("Node Type: %s\n", node.getNodeType());
        System.out.format("Start Time: %s\n", node.getStartTime());
        System.out.format("Fixed Price: %s\n", node.getFixedPrice());
        System.out.format("Offering Type: %s\n", node.getOfferingType());
        System.out.format("Duration: %s\n", node.getDuration());
    }
}
// snippet-end:[redshift.java.ListAndPurchaseReservedNodeOffering.complete]