// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ResolveOpsItem.java demonstrates how to resolve an OpsItem for Amazon Simple Systems Management (Amazon SSM).]
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

package com.example.ssm;

// snippet-start:[ssm.Java2.resolve_ops.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SsmException;
import software.amazon.awssdk.services.ssm.model.UpdateOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.OpsItemStatus;
// snippet-end:[ssm.Java2.resolve_ops.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ResolveOpsItem {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ResolveOpsItem <opsID>\n\n" +
                "Where:\n" +
                "    opsID - the Ops item ID value.\n";

        if (args.length != 1) {
          System.out.println(USAGE);
          System.exit(1);
         }

        /* Read the name from command args */
        String opsID = args[0];

        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

        setOpsItemStatus(ssmClient, opsID);
    }

    // snippet-start:[ssm.Java2.resolve_ops.main]
    public static void setOpsItemStatus(SsmClient ssmClient, String opsID) {

       try {
            UpdateOpsItemRequest opsItemRequest = UpdateOpsItemRequest.builder()
                .opsItemId(opsID)
                .status(OpsItemStatus.RESOLVED)
                .build();

            ssmClient.updateOpsItem(opsItemRequest);

       } catch (SsmException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
    }
    // snippet-end:[ssm.Java2.resolve_ops.main]
}