//snippet-sourcedescription:[UpdateAccessKey.java demonstrates how to update the status of an access key for an IAM user.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS IAM]
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

// snippet-start:[iam.java2.update_access_key.import]
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.StatusType;
import software.amazon.awssdk.services.iam.model.UpdateAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.UpdateAccessKeyResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
// snippet-end:[iam.java2.update_access_key.import]

/**
 * Updates the status of an IAM user's access key
 */
public class UpdateAccessKey {

   private static StatusType statusType;

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a username, access key id and status\n" +
                        "Ex: UpdateAccessKey <username> <access-key-id> <Activate|Inactive>\n";
        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
       }

        String username = args[0];
        String accessId = args[1];
        String status = args[2];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();
       }

        // snippet-start:[iam.java2.update_access_key.main]
       public static void updateKey(IamClient iam, String username, String accessId, String status ) {

          try {
              if (status.toLowerCase().equalsIgnoreCase("active")) {
                  statusType = StatusType.ACTIVE;
              } else if (status.toLowerCase().equalsIgnoreCase("inactive")) {
                  statusType = StatusType.INACTIVE;
              } else {
                  statusType = StatusType.UNKNOWN_TO_SDK_VERSION;
              }
              UpdateAccessKeyRequest request = UpdateAccessKeyRequest.builder()
                .accessKeyId(accessId)
                .userName(username)
                .status(statusType)
                .build();

            UpdateAccessKeyResponse response = iam.updateAccessKey(request);
            // snippet-end:[iam.java2.update_access_key.main]

            System.out.printf(
                "Successfully updated status of access key %s to" +
                        "status %s for user %s", accessId, status, username);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
}
