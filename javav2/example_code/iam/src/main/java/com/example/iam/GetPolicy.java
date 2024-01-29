// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iam;

// snippet-start:[iam.java2.get_policy.main]
// snippet-start:[iam.java2.get_policy.import]
import software.amazon.awssdk.services.iam.model.GetPolicyRequest;
import software.amazon.awssdk.services.iam.model.GetPolicyResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.get_policy.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetPolicy {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <policyArn>\s

                Where:
                    policyArn - A policy ARN that you can obtain from the AWS Management Console.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String policyArn = args[0];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .build();

        getIAMPolicy(iam, policyArn);
        System.out.println("Done");
        iam.close();
    }

    public static void getIAMPolicy(IamClient iam, String policyArn) {
        try {
            GetPolicyRequest request = GetPolicyRequest.builder()
                    .policyArn(policyArn)
                    .build();

            GetPolicyResponse response = iam.getPolicy(request);
            System.out.format("Successfully retrieved policy %s",
                    response.policy().policyName());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[iam.java2.get_policy.main]
