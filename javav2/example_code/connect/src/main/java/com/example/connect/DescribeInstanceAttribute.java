//snippet-sourcedescription:[DescribeInstanceAttribute.java demonstrates how to describe the specified instance attributes.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.DescribeInstanceAttributeRequest;
import software.amazon.awssdk.services.connect.model.DescribeInstanceAttributeResponse;
import software.amazon.awssdk.services.connect.model.InstanceAttributeType;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeInstanceAttribute {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId>\n\n" +
            "Where:\n" +
            "   instanceId - The identifier of the Amazon Connect instance.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        describeAttribute(connectClient, instanceId);
    }

    // snippet-start:[connect.java2.describe.attr.main]
    public static void describeAttribute(ConnectClient connectClient, String instanceId) {
        try{
            DescribeInstanceAttributeRequest request = DescribeInstanceAttributeRequest.builder()
                .instanceId(instanceId)
                .attributeType(InstanceAttributeType.USE_CUSTOM_TTS_VOICES)
                .build();

            DescribeInstanceAttributeResponse response = connectClient.describeInstanceAttribute(request);
            System.out.println("The attribute value is "+response.attribute().attributeType().toString());

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.describe.attr.main]
}
