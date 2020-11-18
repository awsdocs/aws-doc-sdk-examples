//snippet-sourcedescription:[DetachRolePolicy.java demonstrates how to detach a policy from an AWS Identity and Access Management (IAM) role.]
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

// snippet-start:[iam.java2.detach_role_policy.import]
import software.amazon.awssdk.services.iam.model.DetachRolePolicyRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.detach_role_policy.import]

public class DetachRolePolicy {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DetachRolePolicy <roleName> <policyArn> \n\n" +
                "Where:\n" +
                "    roleName - a role name that you can obtain from the AWS Management Console. \n\n" +
                "    policyArn - a policy ARN that you can obtain from the AWS Management Console. \n\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String roleName = args[0];
        String policyArn = args[1];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .build();

        detachPolicy(iam, roleName, policyArn);
        System.out.println("Done");
        iam.close();
    }

    // snippet-start:[iam.java2.detach_role_policy.main]
    public static void detachPolicy(IamClient iam, String roleName, String policyArn ) {

        try {
            DetachRolePolicyRequest request = DetachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(policyArn)
                    .build();

            iam.detachRolePolicy(request);
            System.out.println("Successfully detached policy " + policyArn +
                " from role " + roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[iam.java2.detach_role_policy.main]
    }
}
