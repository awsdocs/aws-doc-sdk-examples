//snippet-sourcedescription:[DetachRolePolicy.java demonstrates how to detach a policy from an IAM role.]
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
// snippet-start:[iam.java2.detach_role_policy.complete]
// snippet-start:[iam.java2.detach_role_policy.import]
import software.amazon.awssdk.services.iam.model.DetachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.DetachRolePolicyResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;

// snippet-end:[iam.java2.detach_role_policy.import]
/**
 * Detaches a policy from a role
 */
public class DetachRolePolicy {
    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a role name and policy arn\n" +
                        "Ex: DetachRolePolicy <role-name> <policy-arn>>\n";

       if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
       }

        String roleName = args[0];
        String policyArn = args[1];

        // snippet-start:[iam.java2.detach_role_policy.main]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        try {

            DetachRolePolicyRequest request = DetachRolePolicyRequest.builder()
                .roleName(roleName)
                .policyArn(policyArn).build();

             DetachRolePolicyResponse response = iam.detachRolePolicy(request);
             // snippet-end:[iam.java2.detach_role_policy.main]

            System.out.println("Successfully detached policy " + policyArn +
                " from role " + roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
}
// snippet-end:[iam.java2.detach_role_policy.complete]
