// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetPolicyResult;

/**
 * Gets an IAM policy's details
 */
public class GetPolicy {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a policy arn\n" +
                "Ex: GetPolicy <policy-arn>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String policy_arn = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        GetPolicyRequest request = new GetPolicyRequest()
                .withPolicyArn(policy_arn);

        GetPolicyResult response = iam.getPolicy(request);

        System.out.format("Successfully retrieved policy %s",
                response.getPolicy().getPolicyName());
    }
}
