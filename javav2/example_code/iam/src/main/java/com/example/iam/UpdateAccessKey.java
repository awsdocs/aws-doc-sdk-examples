//snippet-sourcedescription:[UpdateAccessKey.java demonstrates how to update the status of an access key for an AWS Identity and Access Management (AWS IAM) user.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.update_access_key.import]
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.StatusType;
import software.amazon.awssdk.services.iam.model.UpdateAccessKeyRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
// snippet-end:[iam.java2.update_access_key.import]

public class UpdateAccessKey {

   private static StatusType statusType;

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateAccessKey <username> <accessId> <status> \n\n" +
                "Where:\n" +
                "    username - the name of the user whose key you want to update. \n\n" +
                "    accessId - the access key ID of the secret access key you want to update. \n\n" +
                "    status - the status you want to assign to the secret access key. \n\n" ;

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
       }

        String username = args[0];
        String accessId = args[1];
        String status = args[2];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
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