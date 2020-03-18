//snippet-sourcedescription:[AttachRolePolicy.java demonstrates how to attach a policy to an existing IAM role.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.iam;
// snippet-start:[iam.java2.attach_role_policy.complete]
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

    public static final String POLICY_ARN =
            "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess";

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a role name that you can obtain from the AWS Console\n" +
                        "Ex: AttachRolePolicy <role-name>\n";

         if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String roleName = args[0];

        // snippet-start:[iam.java2.attach_role_policy.main]
        // snippet-start:[iam.java2.attach_role_policy.client]        

            Region region = Region.AWS_GLOBAL;
            IamClient iam = IamClient.builder()
                    .region(region)
                    .build();

        try {
            // snippet-end:[iam.java2.attach_role_policy.client]
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

            // snippet-end:[iam.java2.attach_role_policy.main]
            // snippet-start:[iam.java2.attach_role_policy.attach]
            AttachRolePolicyRequest attachRequest =
                AttachRolePolicyRequest.builder()
                        .roleName(roleName)
                        .policyArn(POLICY_ARN).build();

            iam.attachRolePolicy(attachRequest);

            // snippet-end:[iam.java2.attach_role_policy.attach]
            System.out.println("Successfully attached policy " + POLICY_ARN +
                " to role " + roleName);

         } catch (IamException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
          }

     System.out.println("Done");
    }
}
// snippet-end:[iam.java2.attach_role_policy.complete]
