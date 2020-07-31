//snippet-sourcedescription:[DescribeClusters.java demonstrates how to describe Amazon Redshift clusters.]
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

// snippet-start:[redshift.java2.describe_cluster.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.DescribeClustersResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import java.util.List;
// snippet-end:[redshift.java2.describe_cluster.import]

public class DescribeClusters {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
                .region(region)
                .build();

        describeRedshiftClusters(redshiftClient) ;
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
    // snippet-end:[redshift.java2.describe_cluster.main]
    }

}
