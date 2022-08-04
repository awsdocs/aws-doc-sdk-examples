//snippet-sourcedescription:[DescribeInstanceTags.java demonstrates how to describe the specified tags for your Amazon Elastic Compute Cloud (Amazon EC2) resource.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ec2;

// snippet-start:[ec2.java2.describe_tags.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.DescribeTagsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.DescribeTagsRequest;
// snippet-end:[ec2.java2.describe_tags.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeInstanceTags {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "   <resourceId> \n\n" +
            "Where:\n" +
            "   resourceId - The instance ID value that you can obtain from the AWS Management Console (for example, i-xxxxxx0913e05f482). \n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String resourceId = args[0];
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeEC2Tags(ec2, resourceId);
        ec2.close();
    }

    // snippet-start:[ec2.java2.describe_tags.main]
    public static void describeEC2Tags(Ec2Client ec2, String resourceId) {

        try {
            Filter filter = Filter.builder()
                .name("resource-id")
                .values(resourceId)
                .build();

            DescribeTagsResponse response = ec2.describeTags(DescribeTagsRequest.builder().filters(filter).build());
            response.tags().forEach(tag -> {
                System.out.println("Tag key is: "+tag.key());
                System.out.println("Tag value is: "+tag.value());
            });

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ec2.java2.describe_tags.main]
}
