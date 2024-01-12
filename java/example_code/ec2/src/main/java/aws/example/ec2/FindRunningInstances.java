// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ec2.java1.running_instances.complete]
package aws.example.ec2;

// snippet-start:[ec2.java1.running_instances.import]
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.SdkClientException;

// snippet-end:[ec2.java1.running_instances.import]

public class FindRunningInstances {

    public static void main(String[] args) {

        // snippet-start:[ec2.java1.running_instances.main]
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        try {
            // Create the Filter to use to find running instances
            Filter filter = new Filter("instance-state-name");
            filter.withValues("running");

            // Create a DescribeInstancesRequest
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request.withFilters(filter);

            // Find the running instances
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {

                for (Instance instance : reservation.getInstances()) {

                    // Print out the results
                    System.out.printf(
                            "Found reservation with id %s, " +
                                    "AMI %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
            }
            System.out.print("Done");

        } catch (SdkClientException e) {
            e.getStackTrace();
        }
        // snippet-end:[ec2.java1.running_instances.main]
    }
}
// snippet-end:[ec2.java1.running_instances.complete]
