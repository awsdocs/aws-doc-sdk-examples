// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sts;

// snippet-start:[sts.java2.get_access_key.main]
// snippet-start:[sts.java2.get_access_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;
// snippet-end:[sts.java2.get_access_key.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetAccessKeyInfo {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <accessKeyId>\s

                Where:
                    accessKeyId - The identifier of an access key (for example, XXXXX3JWY3BXW7POHDLA).\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String accessKeyId = args[0];
        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getKeyInfo(stsClient, accessKeyId);
        stsClient.close();
    }

    public static void getKeyInfo(StsClient stsClient, String accessKeyId) {
        try {
            GetAccessKeyInfoRequest accessRequest = GetAccessKeyInfoRequest.builder()
                    .accessKeyId(accessKeyId)
                    .build();

            GetAccessKeyInfoResponse accessResponse = stsClient.getAccessKeyInfo(accessRequest);
            System.out.println("The account associated with the access key is " + accessResponse.account());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sts.java2.get_access_key.main]