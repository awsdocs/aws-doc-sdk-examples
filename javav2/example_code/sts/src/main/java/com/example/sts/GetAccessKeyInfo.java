// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetAccessKeyInfo.java demonstrates how to return the account identifier for the specified access key ID by using AWS Security Token Service (AWS STS).]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Security Token Service (AWS STS)]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sts;

// snippet-start:[sts.java2.get_access_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;
// snippet-end:[sts.java2.get_access_key.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetAccessKeyInfo {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetAccessKeyInfo <accessKeyId> \n\n" +
                "Where:\n" +
                "    accessKeyId - the identifier of an access key (for example, XXXXX3JWY3BXW7POHDLA). \n";

        if (args.length != 1) {
             System.out.println(USAGE);
             System.exit(1);
        }

        String accessKeyId = args[0];
        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getKeyInfo(stsClient, accessKeyId );
        stsClient.close();
    }

    // snippet-start:[sts.java2.get_access_key.main]
    public static void getKeyInfo(StsClient stsClient, String accessKeyId ) {

        try {
            GetAccessKeyInfoRequest accessRequest = GetAccessKeyInfoRequest.builder()
                    .accessKeyId(accessKeyId)
                    .build();

            GetAccessKeyInfoResponse accessResponse = stsClient.getAccessKeyInfo(accessRequest);
            System.out.println("The account associated with the access key is "+accessResponse.account());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sts.java2.get_access_key.main]
}