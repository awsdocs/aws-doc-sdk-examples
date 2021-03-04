// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateOpsItem.java demonstrates how to create a new OpsItem for Amazon Simple Systems Management (Amazon SSM).]
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

//snippet-start:[ssm.java2.create_ops.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.CreateOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.CreateOpsItemResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;
//snippet-end:[ssm.java2.create_ops.import]

public class CreateOpsItem {

    public static void main(String[] args) {

            final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateOpsItem <title> <source> <category> <severity>\n\n" +
                "Where:\n" +
                "    title - the OpsItem title.\n" +
                "    source - the origin of the OpsItem, such as Amazon EC2 or AWS Systems Manager.\n" +
                "    category - a category to assign to an OpsItem.\n" +
                "    severity - a severity to assign to an OpsItem.\n";

            if (args.length != 4) {
                System.out.println(USAGE);
                System.exit(1);
            }

            String title = args[0];
            String source = args[1];
            String category = args[2];
            String severity = args[3];

            Region region = Region.US_EAST_1;
            SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

            System.out.println("The Id of the OpsItem is " +createNewOpsItem(ssmClient, title, source, category, severity));
            ssmClient.close();
    }

    //snippet-start:[ssm.java2.create_ops.main]
    public static String createNewOpsItem( SsmClient ssmClient,
                                           String title,
                                           String source,
                                           String category,
                                           String severity) {

        try {
            CreateOpsItemRequest opsItemRequest = CreateOpsItemRequest.builder()
                .description("Created by the SSM Java API")
                .title(title)
                .source(source)
                .category(category)
                .severity(severity)
                .build();

            CreateOpsItemResponse itemResponse = ssmClient.createOpsItem(opsItemRequest);
            return itemResponse.opsItemId();

    } catch (SsmException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
        return "";
  }
    //snippet-end:[ssm.java2.create_ops.main]
}
