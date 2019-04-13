//snippet-sourcedescription:[AttachRolePolicy.java demonstrates how to attach a policy to an existing IAM role.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
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
package com.example.iam;
// snippet-start:[iam.java.attach_role_policy.complete]
// snippet-start:[iam.java.attach_role_policy.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
// snippet-end:[iam.java.attach_role_policy.import]

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
        // snippet-start:[iam.java.attach_role_policy.main]
        // snippet-start:[iam.java.attach_role_policy.client]        
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();
        // snippet-end:[iam.java.attach_role_policy.client]
        
        List<AttachedPolicy> matching_policies = new ArrayList<>();

        boolean done = false;
        String new_marker = null;

        while(!done) {

        	ListAttachedRolePoliciesResponse response;

        	if (new_marker == null) {
        		ListAttachedRolePoliciesRequest request =
                        ListAttachedRolePoliciesRequest.builder()
                            .roleName(role_name).build();
        		response = iam.listAttachedRolePolicies(request);
        	}
        	else {
        		ListAttachedRolePoliciesRequest request =
                        ListAttachedRolePoliciesRequest.builder()
                            .roleName(role_name)
                            .marker(new_marker).build();
        		response = iam.listAttachedRolePolicies(request);
        	}

            matching_policies.addAll(
                    response.attachedPolicies()
                            .stream()
                            .filter(p -> p.policyName().equals(role_name))
                            .collect(Collectors.toList()));

            if(!response.isTruncated()) {
                done = true;
            }
            else {
            	new_marker = response.marker();
            }
        }

        if (matching_policies.size() > 0) {
            System.out.println(role_name +
                    " policy is already attached to this role.");
            return;
        }

        // snippet-end:[iam.java.attach_role_policy.main]
        // snippet-start:[iam.java.attach_role_policy.attach]
        AttachRolePolicyRequest attach_request =
            AttachRolePolicyRequest.builder()
                .roleName(role_name)
                .policyArn(POLICY_ARN).build();

        iam.attachRolePolicy(attach_request);

        // snippet-end:[iam.java.attach_role_policy.attach]
        System.out.println("Successfully attached policy " + POLICY_ARN +
                " to role " + role_name);       
    }
}
 

// snippet-end:[iam.java.attach_role_policy.complete]
