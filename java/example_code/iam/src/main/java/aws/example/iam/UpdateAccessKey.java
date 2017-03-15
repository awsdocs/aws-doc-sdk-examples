/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import com.amazonaws.services.identitymanagement.model.UpdateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.UpdateAccessKeyResult;

/**
 * Updates the status of an IAM user's access key
 */
public class UpdateAccessKey {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a username, access key id and status\n" +
            "Ex: UpdateAccessKey <username> <access-key-id> <Activate|Inactive>\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];
        String access_id = args[1];
        String status = args[2];

        final AmazonIdentityManagement iam =
            AmazonIdentityManagementClientBuilder.defaultClient();

        UpdateAccessKeyRequest request = new UpdateAccessKeyRequest()
            .withAccessKeyId(access_id)
            .withUserName(username)
            .withStatus(status);

        UpdateAccessKeyResult response = iam.updateAccessKey(request);

        System.out.printf(
                "Successfully updated status of access key %s to" +
                "status %s for user %s", access_id, status, username);
    }
}

