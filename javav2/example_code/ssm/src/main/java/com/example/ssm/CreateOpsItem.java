// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateOpsItem.java demonstrates how to create a new OpsItem for Amazon Simple Systems Management (Amazon SSM).]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Systems Management]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ssm;

//snippet-start:[ssm.java2.create_ops.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.CreateOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.CreateOpsItemResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;
//snippet-end:[ssm.java2.create_ops.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateOpsItem {

    public static void main(String[] args) {

            final String USAGE = "\n" +
                "Usage:\n" +
                "    <title> <source> <category> <severity>\n\n" +
                "Where:\n" +
                "    title - The OpsItem title.\n" +
                "    source - The origin of the OpsItem, such as Amazon EC2 or AWS Systems Manager.\n" +
                "    category - A category to assign to an OpsItem.\n" +
                "    severity - A severity to assign to an OpsItem.\n";

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
                .credentialsProvider(ProfileCredentialsProvider.create())
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
