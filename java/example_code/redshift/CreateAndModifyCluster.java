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

// snippet-sourcedescription:[CreateAndModifyCluster demonstrates how to create and modify an Amazon Redshift cluster.]
// snippet-service:[redshift]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Redshift]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateCluster]
// snippet-keyword:[DescribeClusters]
// snippet-keyword:[ModifyCluster]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2015-02-19]
// snippet-sourceauthor:[AWS]
// snippet-start:[redshift.java.CreateAndModifyCluster.complete]

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.redshift.model.*;

public class CreateAndModifyCluster {

    public static AmazonRedshiftClient client;
    public static String clusterIdentifier = "***provide a cluster identifier***";
    public static long sleepTime = 20;

    public static void main(String[] args) throws IOException {

        AWSCredentials credentials = new PropertiesCredentials(
                CreateAndModifyCluster.class
                        .getResourceAsStream("AwsCredentials.properties"));

        client = new AmazonRedshiftClient(credentials);

        try {
             createCluster();
             waitForClusterReady();
             describeClusters();
             modifyCluster();
             describeClusters();
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }

    private static void createCluster() {
       CreateClusterRequest request = new CreateClusterRequest()
          .withClusterIdentifier(clusterIdentifier)
          .withMasterUsername("masteruser")
          .withMasterUserPassword("12345678Aa")
          .withNodeType("ds1.xlarge")
          .withNumberOfNodes(2);

       Cluster createResponse = client.createCluster(request);
       System.out.println("Created cluster " + createResponse.getClusterIdentifier());
    }

    private static void describeClusters() {
       DescribeClustersRequest request = new DescribeClustersRequest()
          .withClusterIdentifier(clusterIdentifier);

       DescribeClustersResult result = client.describeClusters(request);
       printResult(result);
    }

    private static void modifyCluster() {
       ModifyClusterRequest request = new ModifyClusterRequest()
          .withClusterIdentifier(clusterIdentifier)
          .withPreferredMaintenanceWindow("wed:07:30-wed:08:00");

       client.modifyCluster(request);
       System.out.println("Modified cluster " + clusterIdentifier);

    }

    private static void printResult(DescribeClustersResult result)
    {
        if (result == null)
        {
            System.out.println("Describe clusters result is null.");
            return;
        }

        System.out.println("Cluster property:");
        System.out.format("Preferred Maintenance Window: %s\n", result.getClusters().get(0).getPreferredMaintenanceWindow());
    }

    private static void waitForClusterReady() throws InterruptedException {
        Boolean clusterReady = false;
        System.out.println("Wating for cluster to become available.");
        while (!clusterReady) {
            DescribeClustersResult result = client.describeClusters(new DescribeClustersRequest()
                .withClusterIdentifier(clusterIdentifier));
            String status = (result.getClusters()).get(0).getClusterStatus();
            if (status.equalsIgnoreCase("available")) {
                clusterReady = true;
            }
            else {
                System.out.print(".");
                Thread.sleep(sleepTime*1000);
            }
        }
    }
}
// snippet-end:[redshift.java.CreateAndModifyCluster.complete]