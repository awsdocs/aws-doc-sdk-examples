// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetOpsItem.java demonstrates how to get information about an OpsItem for Amazon Simple Systems Management (Amazon SSM).]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Systems Management]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[ssm.Java2.get_ops.complete]
package com.example.ssm;

// snippet-start:[ssm.Java2.get_ops.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetOpsItemResponse;
import software.amazon.awssdk.services.ssm.model.GetOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.OpsItem;
import software.amazon.awssdk.services.ssm.model.SsmException;
// snippet-end:[ssm.Java2.get_ops.import]

public class GetOpsItem {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetSimpleSystemsManagementOps <opsID>\n\n" +
                "Where:\n" +
                "    opsID - the Ops item ID value.\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String opsID = args[0];
        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
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
        // snippet-end:[ssm.Java2.get_ops.main]
    }
}
// snippet-end:[ssm.Java2.get_ops.complete]

