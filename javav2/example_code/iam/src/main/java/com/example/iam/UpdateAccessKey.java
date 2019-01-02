//snippet-sourcedescription:[UpdateAccessKey.java demonstrates how to update the status of an access key for an IAM user.]
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
import software.amazon.awssdk.services.iam.model.StatusType;
import software.amazon.awssdk.services.iam.model.UpdateAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.UpdateAccessKeyResponse;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

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

        StatusType statusType;

        if (status.toLowerCase().equalsIgnoreCase("active")) {
        	statusType = StatusType.ACTIVE;
        }
        else if (status.toLowerCase().equalsIgnoreCase("inactive")) {
        	statusType = StatusType.INACTIVE;
        }
        else {
        	statusType = StatusType.UNKNOWN_TO_SDK_VERSION;
        }

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        UpdateAccessKeyRequest request = UpdateAccessKeyRequest.builder()
            .accessKeyId(access_id)
            .userName(username)
            .status(statusType)
            .build();

        UpdateAccessKeyResponse response = iam.updateAccessKey(request);

        System.out.printf(
                "Successfully updated status of access key %s to" +
                "status %s for user %s", access_id, status, username);
    }
}
