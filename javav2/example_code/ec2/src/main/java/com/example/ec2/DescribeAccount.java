//snippet-sourcedescription:[DescribeAccount.java demonstrates how to get information about the Amazon Elastic Compute Cloud (Amazon EC2) account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ec2;

// snippet-start:[ec2.java2.describe_account.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeAccountAttributesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_account.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeAccount {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeEC2Account(ec2);
        System.out.print("Done");
        ec2.close();
     }

     // snippet-start:[ec2.java2.describe_account.main]
     public static void describeEC2Account(Ec2Client ec2) {

        try{
            DescribeAccountAttributesResponse accountResults = ec2.describeAccountAttributes();
            accountResults.accountAttributes().forEach(attribute -> {
                        System.out.print("\n The name of the attribute is "+attribute.attributeName());

                            attribute.attributeValues().forEach(myValue ->
                                System.out.print("\n The value of the attribute is "+myValue.attributeValue()));
                            }
            );

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ec2.java2.describe_account.main]
}
