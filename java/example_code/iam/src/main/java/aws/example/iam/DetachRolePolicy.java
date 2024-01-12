// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyResult;

/**
 * Detaches a policy from a role
 */
public class DetachRolePolicy {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a role name and policy arn\n" +
                "Ex: DetachRolePolicy <role-name> <policy-arn>>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String role_name = args[0];
        String policy_arn = args[1];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        DetachRolePolicyRequest request = new DetachRolePolicyRequest()
                .withRoleName(role_name)
                .withPolicyArn(policy_arn);

        DetachRolePolicyResult response = iam.detachRolePolicy(request);

        System.out.println("Successfully detached policy " + policy_arn +
                " from role " + role_name);
    }
}
