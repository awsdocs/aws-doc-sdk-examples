// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sts;

// snippet-start:[sts.java2.get_session_token.main]
// snippet-start:[sts.java2.get_session_token.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
// snippet-end:[sts.java2.get_session_token.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetSessionToken {
    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getToken(stsClient);
        stsClient.close();
    }

    public static void getToken(StsClient stsClient) {
        try {
            GetSessionTokenRequest tokenRequest = GetSessionTokenRequest.builder()
                    .durationSeconds(1500)
                    .build();

            GetSessionTokenResponse tokenResponse = stsClient.getSessionToken(tokenRequest);
            System.out.println("The token value is " + tokenResponse.credentials().sessionToken());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sts.java2.get_session_token.main]