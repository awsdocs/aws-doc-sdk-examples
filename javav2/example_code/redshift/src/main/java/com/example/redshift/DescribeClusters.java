//snippet-sourcedescription:[DescribeClusters.java demonstrates how to describe Amazon Redshift clusters.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshift;

// snippet-start:[redshift.java2.describe_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.DescribeClustersResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import java.util.List;
// snippet-end:[redshift.java2.describe_cluster.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeClusters {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeRedshiftClusters(redshiftClient) ;
        redshiftClient.close();
    }

    // snippet-start:[redshift.java2.describe_cluster.main]
    public static void describeRedshiftClusters(RedshiftClient redshiftClient) {

       try {
            DescribeClustersResponse clusterResponse = redshiftClient.describeClusters();
            List<Cluster> clusterList = clusterResponse.clusters();
            for (Cluster cluster: clusterList) {
                System.out.println("Cluster database name is: "+cluster.dbName());
                System.out.println("Cluster status is: "+cluster.clusterStatus());
            }

       } catch (RedshiftException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
   }
    // snippet-end:[redshift.java2.describe_cluster.main]

}
