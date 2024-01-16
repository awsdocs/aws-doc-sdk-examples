// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttachRolePolicy {

    public static final String POLICY_ARN = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess";

    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a role name\n" +
                "Ex: AttachRolePolicy <role-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String role_name = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        ListAttachedRolePoliciesRequest request = new ListAttachedRolePoliciesRequest()
                .withRoleName(role_name);

        List<AttachedPolicy> matching_policies = new ArrayList<>();

        boolean done = false;

        while (!done) {
            ListAttachedRolePoliciesResult response = iam.listAttachedRolePolicies(request);

            matching_policies.addAll(
                    response.getAttachedPolicies()
                            .stream()
                            .filter(p -> p.getPolicyName().equals(role_name))
                            .collect(Collectors.toList()));

            if (!response.getIsTruncated()) {
                done = true;
            }
            request.setMarker(response.getMarker());
        }

        if (matching_policies.size() > 0) {
            System.out.println(role_name +
                    " policy is already attached to this role.");
            return;
        }

        AttachRolePolicyRequest attach_request = new AttachRolePolicyRequest()
                .withRoleName(role_name)
                .withPolicyArn(POLICY_ARN);

        iam.attachRolePolicy(attach_request);

        System.out.println("Successfully attached policy " + POLICY_ARN +
                " to role " + role_name);
    }
}
