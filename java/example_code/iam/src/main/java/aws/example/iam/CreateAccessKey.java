// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;

/**
 * Creates an access key for an IAM user
 */
public class CreateAccessKey {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an IAM user\n" +
                "Ex: CreateAccessKey <user>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String user = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        CreateAccessKeyRequest request = new CreateAccessKeyRequest()
                .withUserName(user);

        CreateAccessKeyResult response = iam.createAccessKey(request);

        System.out.println("Created access key: " + response.getAccessKey());
    }
}
