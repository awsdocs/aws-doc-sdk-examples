//snippet-sourcedescription:[GetRole.java demonstrates how to get information about the specified AWS Identity and Access Management (IAM) role.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[IAM]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.get_policy.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.iam.model.GetRoleRequest;
import software.amazon.awssdk.services.iam.model.GetRoleResponse;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
// snippet-end:[iam.java2.get_policy.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetRole {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <policyArn> \n\n" +
            "Where:\n" +
            "    policyArn - A policy ARN that you can obtain from the AWS Management Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String roleName = args[0];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getRoleInformation(iam, roleName);
        System.out.println("Done");
        iam.close();
    }

    public static void getRoleInformation(IamClient iam, String roleName) {

        try {
            GetRoleRequest roleRequest = GetRoleRequest.builder()
                .roleName(roleName)
                .build();

            GetRoleResponse response = iam.getRole(roleRequest) ;
            System.out.println("The ARN of the role is " +response.role().arn());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}