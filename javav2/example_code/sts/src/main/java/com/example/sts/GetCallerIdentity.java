// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetCallerIdentity.java demonstrates how to obtain details about the IAM user whose credentials are used to call the operation.]
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

// snippet-start:[sts.java2.get_call_id.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.StsException;
// snippet-end:[sts.java2.get_call_id.import]

public class GetCallerIdentity {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getCallerId(stsClient);
        stsClient.close();
    }

    // snippet-start:[sts.java2.get_call_id.main]
    public static void getCallerId(StsClient stsClient) {

        try {
            GetCallerIdentityResponse response = stsClient.getCallerIdentity();

            System.out.println("The user id is" +response.userId());
            System.out.println("The ARN value is" +response.arn());

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sts.java2.get_call_id.main]
}
