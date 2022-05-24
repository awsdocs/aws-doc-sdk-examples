// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ResolveOpsItem.java demonstrates how to resolve an OpsItem for Amazon Simple Systems Management (Amazon SSM).]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Systems Management]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ssm;

// snippet-start:[ssm.Java2.resolve_ops.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SsmException;
import software.amazon.awssdk.services.ssm.model.UpdateOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.OpsItemStatus;
// snippet-end:[ssm.Java2.resolve_ops.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ResolveOpsItem {

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