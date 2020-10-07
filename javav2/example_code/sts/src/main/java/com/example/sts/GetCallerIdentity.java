// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetCallerIdentity.java demonstrates how to obtain details about the IAM user whose credentials are used to call the operation.]
// snippet-service:[AWS Security Token Service]
// snippet-keyword:[Java]
// snippet-keyword:[AWS Security Token Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-21]
// snippet-sourceauthor:[AWS - scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
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
