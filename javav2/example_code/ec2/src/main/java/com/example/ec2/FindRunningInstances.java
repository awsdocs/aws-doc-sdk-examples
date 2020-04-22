// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[FindRunningInstances.java demonstrates how to use a filter to find running Amazon EC2 instances.]
// snippet-service:[Amazon EC2]
// snippet-keyword:[Java]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2/12/2020]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
 *
 */

package com.example.ec2;

// snippet-start:[ec2.java2.running_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.running_instances.import]

/**
 * Locates all running Amazon EC2 instances by using a filter
 */
public class FindRunningInstances {
    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        findRunningEC2Instances(ec2);
    }

   // snippet-start:[ec2.java2.running_instances.main]
   public static void findRunningEC2Instances(Ec2Client ec2) {

       try {

           String nextToken = null;

           do {
                // Create a Filter object to find all running instances
                Filter filter = Filter.builder()
                    .name("instance-state-name")
                    .values("running")
                    .build();

                // Create a DescribeInstancesRequest object
                DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                    .filters(filter)
                    .build();

                // Find the running instances
                DescribeInstancesResponse response = ec2.describeInstances(request);

                for (Reservation reservation : response.reservations()) {
                    for (Instance instance : reservation.instances()) {
                    System.out.printf(
                            "Found reservation with ID %s, " +
                                    "Amazon Machine Image (AMI) %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.instanceId(),
                            instance.imageId(),
                            instance.instanceType(),
                            instance.state().name(),
                            instance.monitoring().state());
                    System.out.println("");
                }
            }
            nextToken = response.nextToken();

            } while (nextToken != null);

        } catch (Ec2Exception e) {
            e.getStackTrace();
        }
        // snippet-end:[ec2.java2.running_instances.main]
    }
}
