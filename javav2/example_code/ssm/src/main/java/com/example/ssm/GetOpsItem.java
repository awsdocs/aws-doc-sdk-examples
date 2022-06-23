// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetOpsItem.java demonstrates how to get information about an OpsItem for Amazon Simple Systems Management (Amazon SSM).]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Systems Management]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[ssm.Java2.get_ops.complete]
package com.example.ssm;

// snippet-start:[ssm.Java2.get_ops.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetOpsItemResponse;
import software.amazon.awssdk.services.ssm.model.GetOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.OpsItem;
import software.amazon.awssdk.services.ssm.model.SsmException;
// snippet-end:[ssm.Java2.get_ops.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetOpsItem {
    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <opsID>\n\n" +
                "Where:\n" +
                "    opsID - The Ops item ID value.\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String opsID = args[0];
        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getOpsItem(ssmClient, opsID );
        ssmClient.close();
    }

    // snippet-start:[ssm.Java2.get_ops.main]
    public static void getOpsItem(SsmClient ssmClient, String opsID ) {

        try {
            GetOpsItemRequest opsRequest = GetOpsItemRequest.builder()
                    .opsItemId(opsID)
                    .build();

            // Get SSM Parameters (you can define them in the AWS Management Console)
            GetOpsItemResponse opsItem = ssmClient.getOpsItem(opsRequest);
            OpsItem item = opsItem.opsItem();

            System.out.println(item.title());
            System.out.println(item.description());
            System.out.println(item.source());

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ssm.Java2.get_ops.main]
}
// snippet-end:[ssm.Java2.get_ops.complete]

