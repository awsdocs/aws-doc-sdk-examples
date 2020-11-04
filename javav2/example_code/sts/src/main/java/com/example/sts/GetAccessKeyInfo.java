// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetAccessKeyInfo.java demonstrates how to return the account identifier for the specified access key ID.]
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

// snippet-start:[sts.java2.get_access_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;
// snippet-end:[sts.java2.get_access_key.import]

public class GetAccessKeyInfo {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the accessKey Id value.  \n" +
                "\n" +
                "Ex: GetAccessKeyInfo <accessKeyId>\n";

        if (args.length < 1) {
             System.out.println(USAGE);
             System.exit(1);
        }

        /* Read the name from command args*/
        String accessKeyId = args[0];

        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        getKeyInfo(stsClient, accessKeyId );
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