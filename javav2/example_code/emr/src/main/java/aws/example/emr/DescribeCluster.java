//snippet-sourcedescription:[DescribeCluster.java demonstrates how to describe a cluster.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package aws.example.emr;

// snippet-start:[emr.java2.describe_cluster.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.DescribeClusterRequest;
import software.amazon.awssdk.services.emr.model.DescribeClusterResponse;
import software.amazon.awssdk.services.emr.model.EmrException;
// snippet-end:[emr.java2.describe_cluster.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeCluster {

    public static void main(String[] args){

        final String usage = "\n" +
                "Usage: " +
                "   <clusterId> \n\n" +
                "Where:\n" +
                "   clusterId - The identifier of the cluster to describe. \n\n" ;

        if (args.length != 1) {
              System.out.println(usage);
              System.exit(1);
         }

        String clusterId = args[0] ;
        Region region = Region.US_WEST_2;
        EmrClient emrClient = EmrClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeMyCluster(emrClient, clusterId);
        emrClient.close();
    }

    // snippet-start:[emr.java2.describe_cluster.main]
    public static void describeMyCluster(EmrClient emrClient, String clusterId){

        try {
            DescribeClusterRequest clusterRequest = DescribeClusterRequest.builder()
                    .clusterId(clusterId)
                    .build();

            DescribeClusterResponse response = emrClient.describeCluster(clusterRequest);
            System.out.println("The name of the cluster is "+response.cluster().name());

        } catch(EmrException e){
        System.err.println(e.getMessage());
        System.exit(1);
    }
 }
    // snippet-end:[emr.java2.describe_cluster.main]
}
