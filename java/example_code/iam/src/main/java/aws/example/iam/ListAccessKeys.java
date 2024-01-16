// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;

/**
 * List all access keys associated with an IAM user
 */
public class ListAccessKeys {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an IAM  username\n" +
                "Ex: ListAccessKeys <username>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        boolean done = false;
        ListAccessKeysRequest request = new ListAccessKeysRequest()
                .withUserName(username);

        while (!done) {

            ListAccessKeysResult response = iam.listAccessKeys(request);

            for (AccessKeyMetadata metadata : response.getAccessKeyMetadata()) {
                System.out.format("Retrieved access key %s",
                        metadata.getAccessKeyId());
            }

            request.setMarker(response.getMarker());

            if (!response.getIsTruncated()) {
                done = true;
            }
        }
    }
}
