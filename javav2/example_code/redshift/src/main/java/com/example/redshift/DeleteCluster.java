//snippet-sourcedescription:[DeleteCluster.java demonstrates how to delete an Amazon Redshift cluster.]
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

// snippet-start:[redshift.java2.delete_cluster.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.DeleteClusterRequest;
import software.amazon.awssdk.services.redshift.model.DeleteClusterResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
// snippet-end:[redshift.java2.delete_cluster.import]

public class DeleteCluster {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteCluster <clusterId> \n\n" +
                "Where:\n" +
                "    clusterId - The ID of the cluster to delete \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String clusterId = args[0];

        Region region = Region.US_WEST_2;
        RedshiftClient redshiftClient = RedshiftClient.builder()
                .region(region)
                .build();

        deleteRedshiftCluster(redshiftClient, clusterId) ;
    }

    // snippet-start:[redshift.java2.delete_cluster.main]
    public static void deleteRedshiftCluster(RedshiftClient redshiftClient, String clusterId) {

        try {
            DeleteClusterRequest deleteClusterRequest = DeleteClusterRequest.builder()
                    .clusterIdentifier(clusterId)
                    .skipFinalClusterSnapshot(true)
                    .build();

            // Delete the cluster
            DeleteClusterResponse response = redshiftClient.deleteCluster(deleteClusterRequest);
            System.out.println("The status is "+response.cluster().clusterStatus());

        } catch (RedshiftException e) {

            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[redshift.java2.delete_cluster.main]
   }
}
