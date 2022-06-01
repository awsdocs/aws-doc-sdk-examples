//snippet-sourcedescription:[UpdateAccessKey.java demonstrates how to update the status of an access key for an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.update_access_key.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.StatusType;
import software.amazon.awssdk.services.iam.model.UpdateAccessKeyRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
// snippet-end:[iam.java2.update_access_key.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateAccessKey {

   private static StatusType statusType;

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <username> <accessId> <status> \n\n" +
                "Where:\n" +
                "    username - The name of the user whose key you want to update. \n\n" +
                "    accessId - The access key ID of the secret access key you want to update. \n\n" +
                "    status - The status you want to assign to the secret access key. \n\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
       }

        String username = args[0];
        String accessId = args[1];
        String status = args[2];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        updateKey(iam, username, accessId, status);
        System.out.println("Done");
        iam.close();
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

              iam.updateAccessKey(request);

              System.out.printf(
                "Successfully updated the status of access key %s to" +
                        "status %s for user %s", accessId, status, username);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[iam.java2.update_access_key.main]
}