// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.scenerio;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.neptune.NeptuneClient;
import software.amazon.awssdk.services.neptune.model.*;
import software.amazon.awssdk.services.neptune.model.CreateDbClusterRequest;
import software.amazon.awssdk.services.neptune.model.CreateDbClusterResponse;
import software.amazon.awssdk.services.neptune.model.CreateDbInstanceRequest;
import software.amazon.awssdk.services.neptune.model.CreateDbInstanceResponse;
import software.amazon.awssdk.services.neptune.model.CreateDbSubnetGroupRequest;
import software.amazon.awssdk.services.neptune.model.CreateDbSubnetGroupResponse;
import software.amazon.awssdk.services.neptune.model.DBCluster;
import software.amazon.awssdk.services.neptune.model.DBInstance;
import software.amazon.awssdk.services.neptune.model.DeleteDbClusterRequest;
import software.amazon.awssdk.services.neptune.model.DeleteDbInstanceRequest;
import software.amazon.awssdk.services.neptune.model.DeleteDbSubnetGroupRequest;
import software.amazon.awssdk.services.neptune.model.DescribeDbClustersResponse;
import software.amazon.awssdk.services.neptune.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.neptune.model.DescribeDbInstancesResponse;

import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunedata.model.*;

import software.amazon.awssdk.services.neptune.model.DescribeDbClustersRequest;


import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NeptuneActions {

    private final Region region = Region.US_EAST_1;

        private final NeptuneClient neptuneClient = NeptuneClient.builder().region(region).build();

       public void deleteDBSubnetGroup(String subnetGroupId) {
           DeleteDbSubnetGroupRequest deleteDbSubnetGroupRequest = DeleteDbSubnetGroupRequest.builder()
                   .dbSubnetGroupName(subnetGroupId)
                   .build();

           neptuneClient.deleteDBSubnetGroup(deleteDbSubnetGroupRequest);
           System.out.println("Deleted group " + subnetGroupId);
       }

        public void deleteDBCluster(String dbClusterId) {
           DeleteDbClusterRequest deleteDbClusterRequest = DeleteDbClusterRequest.builder()
                   .dbClusterIdentifier(dbClusterId)
                   .skipFinalSnapshot(true)
                   .build();
           neptuneClient.deleteDBCluster(deleteDbClusterRequest);
           System.out.println("Deleted Cluster " + dbClusterId);

       }

    public void describeDBInstances(String instanceIdentifier) {
        DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder()
                .dbInstanceIdentifier(instanceIdentifier)
                .build();

        try {
            DescribeDbInstancesResponse response = neptuneClient.describeDBInstances(request);
            for (DBInstance instance : response.dbInstances()) {
                System.out.println("DB Instance Identifier: " + instance.dbInstanceIdentifier());
                System.out.println("DB Instance Class: " + instance.dbInstanceClass());
                System.out.println("Engine: " + instance.engine());
                System.out.println("Engine Version: " + instance.engineVersion());
                System.out.println("Availability Zone: " + instance.availabilityZone());
                System.out.println("DB Subnet Group: " + instance.dbSubnetGroup().dbSubnetGroupName());
                System.out.println("VPC ID: " + instance.dbSubnetGroup().vpcId());
                System.out.println("Subnet Group Description: " + instance.dbSubnetGroup().dbSubnetGroupDescription());
                System.out.println("Endpoint: " + instance.endpoint().address() + ":" + instance.endpoint().port());
                System.out.println("Storage Type: " + instance.storageType());
                System.out.println("Multi-AZ: " + instance.multiAZ());
                System.out.println("IAM DB Auth Enabled: " + instance.iamDatabaseAuthenticationEnabled());
                System.out.println("Publicly Accessible: " + instance.publiclyAccessible());
                System.out.println("Instance Status: " + instance.dbInstanceStatus());
                System.out.println("Preferred Maintenance Window: " + instance.preferredMaintenanceWindow());
                System.out.println("------");
            }
        } catch (NeptuneException e) {
            System.err.println("Failed to describe DB instances: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        }
    }

    public void describeDBClusters(String clusterId) {
        DescribeDbClustersRequest request = DescribeDbClustersRequest.builder()
                .dbClusterIdentifier(clusterId)
                .build();

        try {
            DescribeDbClustersResponse response = neptuneClient.describeDBClusters(request);
            for (DBCluster cluster : response.dbClusters()) {
                System.out.println("Cluster Identifier: " + cluster.dbClusterIdentifier());
                System.out.println("Status: " + cluster.status());
                System.out.println("Engine: " + cluster.engine());
                System.out.println("Engine Version: " + cluster.engineVersion());
                System.out.println("Endpoint: " + cluster.endpoint());
                System.out.println("Reader Endpoint: " + cluster.readerEndpoint());
                System.out.println("Availability Zones: " + cluster.availabilityZones());
                System.out.println("Subnet Group: " + cluster.dbSubnetGroup());
                System.out.println("VPC Security Groups:");
                cluster.vpcSecurityGroups().forEach(vpcGroup ->
                        System.out.println("  - " + vpcGroup.vpcSecurityGroupId()));
                System.out.println("Storage Encrypted: " + cluster.storageEncrypted());
                System.out.println("IAM DB Auth Enabled: " + cluster.iamDatabaseAuthenticationEnabled());
                System.out.println("Backup Retention Period: " + cluster.backupRetentionPeriod() + " days");
                System.out.println("Preferred Backup Window: " + cluster.preferredBackupWindow());
                System.out.println("Preferred Maintenance Window: " + cluster.preferredMaintenanceWindow());
                System.out.println("------");
            }
        } catch (NeptuneException e) {
            System.err.println("Failed to describe the DB cluster: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        }
    }


    public void deleteDBInstance(String dbInstanceId) {
            DeleteDbInstanceRequest instanceRequest = DeleteDbInstanceRequest.builder()
                    .dbInstanceIdentifier(dbInstanceId)
                    .skipFinalSnapshot(true)
                    .build();

            neptuneClient.deleteDBInstance(instanceRequest);
            System.out.println("Deleted DBInstance " + dbInstanceId);
        }

    public void isNeptuneInstanceReady(String instanceId) {
        try {
            boolean isReady = false;
            int elapsedSeconds = 0;
            String lastStatus = "checking...";

            while (!isReady) {
                for (int i = 0; i < 20 && !isReady; i++) {
                    String line = String.format(
                            "\r⏰ Elapsed: %-20s 🔄 Status: %-20s",
                            formatElapsedTime(elapsedSeconds),
                            lastStatus
                    );
                    System.out.print(line);
                    System.out.flush();

                    Thread.sleep(1000);
                    elapsedSeconds++;
                }

                // Every 20 seconds, check DB instance status
                DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder()
                        .dbInstanceIdentifier(instanceId)
                        .build();

                DescribeDbInstancesResponse response = neptuneClient.describeDBInstances(request);
                List<DBInstance> instances = response.dbInstances();
                if (instances.isEmpty()) {
                    System.out.print("\r❌ No instance found with ID: " + instanceId + "                       \n");
                    break;
                }

                String status = instances.get(0).dbInstanceStatus();
                lastStatus = status;

                if ("available".equalsIgnoreCase(status)) {
                    String doneLine = String.format(
                            "\r✅ Neptune instance is now available after %s.                     \n",
                            formatElapsedTime(elapsedSeconds)
                    );
                    System.out.print(doneLine);
                    isReady = true;
                }
            }

        } catch (NeptuneException e) {
            System.err.println("\n❌ Error checking instance status: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting", e);
        }
    }

    private String formatElapsedTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (minutes > 0) {
            return minutes + (minutes == 1 ? " min" : " mins") + ", " +
                    remainingSeconds + (remainingSeconds == 1 ? " sec" : " secs");
        } else {
            return remainingSeconds + (remainingSeconds == 1 ? " sec" : " secs");
        }
    }





    public String createDBInstance(String dbInstanceId, String dbClusterId) {
        try {
            CreateDbInstanceRequest request = CreateDbInstanceRequest.builder()
                    .dbInstanceIdentifier(dbInstanceId)
                    .dbInstanceClass("db.r5.large")
                    .engine("neptune")
                    .dbClusterIdentifier(dbClusterId)
                    .availabilityZone("us-east-1b")
                    .build();

            CreateDbInstanceResponse response = neptuneClient.createDBInstance(request);
            System.out.println("Created Neptune DB Instance: " + response.dbInstance().dbInstanceIdentifier());
            return response.dbInstance().dbInstanceIdentifier();

        } catch (NeptuneException e) {
            System.err.println("Failed to create Neptune DB instance: " + e.awsErrorDetails().errorMessage());
            return "";
        }
    }

    public String createDBCluster(String dbName) {
        try {
            CreateDbClusterRequest request = CreateDbClusterRequest.builder()
                    .dbClusterIdentifier(dbName)
                    .engine("neptune")
                    .deletionProtection(false)
                    .backupRetentionPeriod(1)
                    .build();

            CreateDbClusterResponse response = neptuneClient.createDBCluster(request);
            System.out.println("DB Cluster created: " + response.dbCluster().dbClusterIdentifier());
            return response.dbCluster().dbClusterIdentifier();

        } catch (NeptuneException e) {
            System.err.println("Failed to create Neptune DB cluster: " + e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public String createSubnetGroup(String vpcId, String groupName) {
        List<String> subnetList = getSubnetIds(vpcId);
        try {
            CreateDbSubnetGroupRequest request = CreateDbSubnetGroupRequest.builder()
                    .dbSubnetGroupName(groupName)
                    .dbSubnetGroupDescription("Subnet group for Neptune cluster")
                    .subnetIds(subnetList)
                    .build();

            CreateDbSubnetGroupResponse response = neptuneClient.createDBSubnetGroup(request);
            System.out.println("Subnet group created: " + response.dbSubnetGroup().dbSubnetGroupName());
            return response.dbSubnetGroup().dbSubnetGroupArn();

        } catch (NeptuneException e) {
            System.err.println("Error creating subnet group: " + e.awsErrorDetails().errorMessage());
            return "";
        }
    }

    private List<String> getSubnetIds(String vpcId) {
        try (Ec2Client ec2 = Ec2Client.builder().region(region).build()) {
            DescribeSubnetsRequest request = DescribeSubnetsRequest.builder()
                    .filters(builder -> builder.name("vpc-id").values(vpcId))
                    .build();

            DescribeSubnetsResponse response = ec2.describeSubnets(request);
            return response.subnets().stream()
                    .map(Subnet::subnetId)
                    .collect(Collectors.toList());
        }
    }
}
