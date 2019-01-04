//snippet-sourcedescription:[AttachRolePolicy.java demonstrates how to attach a policy to a role.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-15]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

    public static final String POLICY_ARN =
        "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess";

    public static void main(String[] args) {
        final String USAGE =
            "To run this example, supply a role name\n" +
            "Ex: AttachRolePolicy <role-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String role_name = args[0];

        final AmazonIdentityManagement iam =
            AmazonIdentityManagementClientBuilder.defaultClient();

        ListAttachedRolePoliciesRequest request =
            new ListAttachedRolePoliciesRequest()
                .withRoleName(role_name);

        List<AttachedPolicy> matching_policies = new ArrayList<>();

        boolean done = false;

        while(!done) {
            ListAttachedRolePoliciesResult response =
                iam.listAttachedRolePolicies(request);

            matching_policies.addAll(
                    response.getAttachedPolicies()
                            .stream()
                            .filter(p -> p.getPolicyName().equals(role_name))
                            .collect(Collectors.toList()));

            if(!response.getIsTruncated()) {
                done = true;
            }
            request.setMarker(response.getMarker());
        }

        if (matching_policies.size() > 0) {
            System.out.println(role_name +
                    " policy is already attached to this role.");
            return;
        }

        AttachRolePolicyRequest attach_request =
            new AttachRolePolicyRequest()
                .withRoleName(role_name)
                .withPolicyArn(POLICY_ARN);

        iam.attachRolePolicy(attach_request);

        System.out.println("Successfully attached policy " + POLICY_ARN +
                " to role " + role_name);
    }
}
