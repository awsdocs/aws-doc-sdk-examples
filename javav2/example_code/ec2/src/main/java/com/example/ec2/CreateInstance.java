 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.ec2;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

/**
 * Creates an EC2 instance
 */
public class CreateInstance
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply an instance name and AMI image id\n" +
            "Ex: CreateInstance <instance-name> <ami-image-id>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        String ami_id = args[1];

        Ec2Client ec2 = Ec2Client.create();

        RunInstancesRequest run_request = RunInstancesRequest.builder()
            .imageId(ami_id)
            .instanceType(InstanceType.T1_MICRO)
            .maxCount(1)
            .minCount(1)
            .build();

        RunInstancesResponse response = ec2.runInstances(run_request);

        String instance_id = response.reservation().reservationId();

        Tag tag = Tag.builder()
            .key("Name")
            .value(name)
            .build();

        CreateTagsRequest tag_request = CreateTagsRequest.builder()
            .tags(tag)
            .build();

        try {
        	ec2.createTags(tag_request);

            System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                instance_id, ami_id);
        }
        catch (Ec2Exception e) {
        	System.err.println(e.getMessage());
        	System.exit(1);
        }
        System.out.println("Done!");
        
    }
}

