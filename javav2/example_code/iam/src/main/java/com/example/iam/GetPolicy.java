//snippet-sourcedescription:[GetPolicy.java demonstrates how to get the details for an AWS Identity and Access Management (IAM) policy.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.get_policy.import]
import software.amazon.awssdk.services.iam.model.GetPolicyRequest;
import software.amazon.awssdk.services.iam.model.GetPolicyResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.get_policy.import]

public class GetPolicy {

    public static void main(String[] args) {

      final String USAGE = "\n" +
                "Usage:\n" +
                "    GetPolicy <policyArn> \n\n" +
                "Where:\n" +
                "    policyArn - a policy ARN that you can obtain from the AWS Management Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
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

    // snippet-start:[iam.java2.get_policy.main]
    public static void getIAMPolicy(IamClient iam, String policyArn) {

        try {

            GetPolicyRequest request = GetPolicyRequest.builder()
                .policyArn(policyArn).build();

            GetPolicyResponse response = iam.getPolicy(request);
            System.out.format("Successfully retrieved policy %s",
                response.policy().policyName());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[iam.java2.get_policy.main]
}
