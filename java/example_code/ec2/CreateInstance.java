/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

/**
 * Creates an EC2 instance
 */
public class CreateInstance {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an instance name and AMI image id\n" +
            "Ex: CreateInstance <instance-name> <ami-image-id>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceName = args[0];
        String amiImageId = args[1];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
            .withImageId(amiImageId)
            .withInstanceType(InstanceType.T1Micro)
            .withMaxCount(1)
            .withMinCount(1);

        RunInstancesResult runInstanceResponse = ec2.runInstances(runInstancesRequest);

        String instanceId = runInstanceResponse.getReservation().getReservationId();

        Tag nameTag = new Tag()
            .withKey("Name")
            .withValue(instanceName);

        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
            .withTags(nameTag);

        CreateTagsResult createTagsResponse = ec2.createTags(createTagsRequest);

        System.out.printf("Successfully started EC2 instance %s based on AMI %s", instanceId, amiImageId);
    }
}
