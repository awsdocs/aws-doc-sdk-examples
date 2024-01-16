// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[redshift.java.CreateAndModifyClusterSubnetGroup.complete]
package com.amazonaws.services.redshift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.redshift.model.*;

public class CreateAndModifyClusterSubnetGroup {

    public static AmazonRedshift client;
    public static String clusterSubnetGroupName = "subnet-group-name";

    // You can use the VPC console to find subnet IDs to use.
    public static String subnetId1 = "***provide a subnet ID****";
    public static String subnetId2 = "***provide a subnet ID****";

    public static void main(String[] args) throws IOException {

        // Default client using the {@link
        // com.amazonaws.auth.DefaultAWSCredentialsProviderChain}
        client = AmazonRedshiftClientBuilder.defaultClient();

        try {
            createClusterSubnetGroup();
            describeClusterSubnetGroups();
            modifyClusterSubnetGroup();
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static void createClusterSubnetGroup() {
        CreateClusterSubnetGroupRequest request = new CreateClusterSubnetGroupRequest()
                .withClusterSubnetGroupName(clusterSubnetGroupName)
                .withDescription("my cluster subnet group")
                .withSubnetIds(subnetId1);
        client.createClusterSubnetGroup(request);
        System.out.println("Created cluster subnet group: " + clusterSubnetGroupName);
    }

    private static void modifyClusterSubnetGroup() {
        // Get existing subnet list.
        DescribeClusterSubnetGroupsRequest request1 = new DescribeClusterSubnetGroupsRequest()
                .withClusterSubnetGroupName(clusterSubnetGroupName);
        DescribeClusterSubnetGroupsResult result1 = client.describeClusterSubnetGroups(request1);
        List<String> subnetNames = new ArrayList<String>();
        // We can work with just the first group returned since we requested info about
        // one group.
        for (Subnet subnet : result1.getClusterSubnetGroups().get(0).getSubnets()) {
            subnetNames.add(subnet.getSubnetIdentifier());
        }
        // Add to existing subnet list.
        subnetNames.add(subnetId2);

        ModifyClusterSubnetGroupRequest request = new ModifyClusterSubnetGroupRequest()
                .withClusterSubnetGroupName(clusterSubnetGroupName)
                .withSubnetIds(subnetNames);
        ClusterSubnetGroup result2 = client.modifyClusterSubnetGroup(request);
        System.out.println("\nSubnet group modified.");
        printResultSubnetGroup(result2);
    }

    private static void describeClusterSubnetGroups() {
        DescribeClusterSubnetGroupsRequest request = new DescribeClusterSubnetGroupsRequest()
                .withClusterSubnetGroupName(clusterSubnetGroupName);

        DescribeClusterSubnetGroupsResult result = client.describeClusterSubnetGroups(request);
        printResultSubnetGroups(result);
    }

    private static void printResultSubnetGroups(DescribeClusterSubnetGroupsResult result) {
        if (result == null) {
            System.out.println("\nDescribe cluster subnet groups result is null.");
            return;
        }

        for (ClusterSubnetGroup group : result.getClusterSubnetGroups()) {
            printResultSubnetGroup(group);
        }

    }

    private static void printResultSubnetGroup(ClusterSubnetGroup group) {
        System.out.format("Name: %s, Description: %s\n", group.getClusterSubnetGroupName(), group.getDescription());
        for (Subnet subnet : group.getSubnets()) {
            System.out.format("  Subnet: %s, %s, %s\n", subnet.getSubnetIdentifier(),
                    subnet.getSubnetAvailabilityZone().getName(), subnet.getSubnetStatus());
        }
    }
}
// snippet-end:[redshift.java.CreateAndModifyClusterSubnetGroup.complete]