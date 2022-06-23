//snippet-sourcedescription:[AttachRolePolicy.java demonstrates how to attach a policy to an existing AWS Identity and Access Management (IAM) role.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.attach_role_policy.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.AttachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesResponse;
import java.util.List;
// snippet-end:[iam.java2.attach_role_policy.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AttachRolePolicy {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <roleName> <policyArn> \n\n" +
                "Where:\n" +
                "    roleName - A role name that you can obtain from the AWS Management Console. \n\n" +
                "    policyArn - A policy ARN that you can obtain from the AWS Management Console. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String roleName = args[0];
        String policyArn = args[1];

        // snippet-start:[iam.java2.attach_role_policy.client]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        // snippet-end:[iam.java2.attach_role_policy.client]

        attachIAMRolePolicy(iam, roleName, policyArn);
        iam.close();
    }

    // snippet-start:[iam.java2.attach_role_policy.main]
    public static void attachIAMRolePolicy(IamClient iam, String roleName, String policyArn ) {

        try {
             ListAttachedRolePoliciesRequest request = ListAttachedRolePoliciesRequest.builder()
                    .roleName(roleName)
                    .build();

            ListAttachedRolePoliciesResponse response = iam.listAttachedRolePolicies(request);
            List<AttachedPolicy> attachedPolicies = response.attachedPolicies();

            // Ensure that the policy is not attached to this role
            String polArn = "";
            for (AttachedPolicy policy: attachedPolicies) {
                polArn = policy.policyArn();
                if (polArn.compareTo(policyArn)==0) {
                   System.out.println(roleName +
                            " policy is already attached to this role.");
                    return;
                }
          }

           // snippet-start:[iam.java2.attach_role_policy.attach]
            AttachRolePolicyRequest attachRequest =
                AttachRolePolicyRequest.builder()
                        .roleName(roleName)
                        .policyArn(policyArn)
                        .build();

            iam.attachRolePolicy(attachRequest);

            // snippet-end:[iam.java2.attach_role_policy.attach]
            System.out.println("Successfully attached policy " + policyArn +
                " to role " + roleName);

         } catch (IamException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
          }

     System.out.println("Done");
    }
    // snippet-end:[iam.java2.attach_role_policy.main]
}
