//snippet-sourcedescription:[AttachRolePolicy.java demonstrates how to attach a policy to an existing AWS Identity and Access Management (IAM) role.]
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

// snippet-start:[iam.java2.attach_role_policy.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.AttachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
// snippet-end:[iam.java2.attach_role_policy.import]

public class AttachRolePolicy {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    AttachRolePolicy <roleName> <policyArn> \n\n" +
                "Where:\n" +
                "    roleName - a role name that you can obtain from the AWS Management Console. \n\n" +
                "    policyArn - a policy ARN that you can obtain from the AWS Management Console. \n\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String roleName = args[0];
        String policyArn = args[1];

        // snippet-start:[iam.java2.attach_role_policy.client]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .build();
        // snippet-end:[iam.java2.attach_role_policy.client]

        attachIAMRolePolicy(iam, roleName, policyArn);
        iam.close();
    }

    // snippet-start:[iam.java2.attach_role_policy.main]
    public static void attachIAMRolePolicy(IamClient iam,String roleName, String policyArn ) {

        try {

            List<AttachedPolicy> matchingPolicies = new ArrayList<>();

            boolean done = false;
            String newMarker = null;

            while(!done) {

                ListAttachedRolePoliciesResponse response;

                if (newMarker == null) {
                    ListAttachedRolePoliciesRequest request =
                        ListAttachedRolePoliciesRequest.builder()
                                .roleName(roleName).build();
                    response = iam.listAttachedRolePolicies(request);
                } else {
                    ListAttachedRolePoliciesRequest request =
                        ListAttachedRolePoliciesRequest.builder()
                                .roleName(roleName)
                                .marker(newMarker).build();
                    response = iam.listAttachedRolePolicies(request);
                }

                matchingPolicies.addAll(
                    response.attachedPolicies()
                            .stream()
                            .filter(p -> p.policyName().equals(roleName))
                            .collect(Collectors.toList()));

                if(!response.isTruncated()) {
                    done = true;

                } else {
                    newMarker = response.marker();
                }
            }

                if (matchingPolicies.size() > 0) {
                    System.out.println(roleName +
                        " policy is already attached to this role.");
                return;
            }

            // snippet-start:[iam.java2.attach_role_policy.attach]
            AttachRolePolicyRequest attachRequest =
                AttachRolePolicyRequest.builder()
                        .roleName(roleName)
                        .policyArn(policyArn).build();

            iam.attachRolePolicy(attachRequest);

            // snippet-end:[iam.java2.attach_role_policy.attach]
            System.out.println("Successfully attached policy " + policyArn +
                " to role " + roleName);

         } catch (IamException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
          }
        // snippet-end:[iam.java2.attach_role_policy.main]
     System.out.println("Done");
    }
}
