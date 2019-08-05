//snippet-sourcedescription:[GetPolicy.java demonstrates how to get the details for an IAM policy.]
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
// snippet-start:[iam.java2.get_policy.complete]
// snippet-start:[iam.java2.get_policy.import]
import software.amazon.awssdk.services.iam.model.GetPolicyRequest;
import software.amazon.awssdk.services.iam.model.GetPolicyResponse;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
 
// snippet-end:[iam.java2.get_policy.import]
/**
 * Gets an IAM policy's details
 */
public class GetPolicy {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a policy arn\n" +
            "Ex: GetPolicy <policy-arn>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String policy_arn = args[0];

        // snippet-start:[iam.java2.get_policy.main]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        GetPolicyRequest request = GetPolicyRequest.builder()
            .policyArn(policy_arn).build();

        GetPolicyResponse response = iam.getPolicy(request);
        // snippet-end:[iam.java2.get_policy.main]

        System.out.format("Successfully retrieved policy %s",
                response.policy().policyName());
    }
}

// snippet-end:[iam.java2.get_policy.complete]
