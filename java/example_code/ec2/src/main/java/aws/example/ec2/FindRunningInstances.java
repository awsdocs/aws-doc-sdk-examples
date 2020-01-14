/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[FindRunningInstances.java demonstrates how to use a Filter to find running instances]
// snippet-service:[ec2]
// snippet-keyword:[Java]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-10]
// snippet-sourceauthor:[AWS-scmacdon]

// snippet-start:[ec2.java1.running_instances.complete]
package aws.example.ec2;

// snippet-start:[ec2.java1.running_instances.import]
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
// snippet-end:[ec2.java1.running_instances.import]

public class FindRunningInstances{

    public static void main(String[] args) {

        // snippet-start:[ec2.java1.running_instances.main]
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        try {
            //Create the Filter to use to find running instances
            Filter filter = new Filter("instance-state-name");
            filter.withValues("running");

            //Create a DescribeInstancesRequest
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request.withFilters(filter);

            // Find the running instances
            DescribeInstancesResult response = ec2.describeInstances(request);

            for (Reservation reservation : response.getReservations()){

                for (Instance instance : reservation.getInstances()) {

                   //Print out the results
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
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
        // snippet-end:[ec2.java1.running_instances.main]
    }
}
// snippet-end:[ec2.java1.running_instances.complete]
