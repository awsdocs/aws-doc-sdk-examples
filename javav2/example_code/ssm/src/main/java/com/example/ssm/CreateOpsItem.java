// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateOpsItem.java demonstrates how to create a new OpsItem.]
// snippet-service:[ssm]
// snippet-keyword:[Java]
// snippet-keyword:[AWS Systems Manager]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-10]
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
                    "    CreateOpsItem <title><source><category><severity>\n\n" +
                    "Where:\n" +
                    "    title - The OpsItem title.\n" +
                    "    source - The origin of the OpsItem, such as Amazon EC2 or AWS Systems Manager.\n" +
                    "    category - A category to assign to an OpsItem.\n" +
                    "    severity - A severity to assign to an OpsItem.\n";

            if (args.length < 4) {
                System.out.println(USAGE);
                System.exit(1);
            }

            /* Read the name from command args */
            String title = args[0];
            String source = args[1];
            String category = args[2];
            String severity = args[3];

            Region region = Region.US_EAST_1;
            SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

            System.out.println("The ID of the OpsItem is " +createNewOpsItem(ssmClient, title, source, category, severity));
    }

    //snippet-start:[ssm.java2.create_ops.main]
    public static String createNewOpsItem( SsmClient ssmClient,
                                           String title,
                                           String source,
                                           String category,
                                           String severity) {

        try {
            CreateOpsItemRequest opsItemRequest = CreateOpsItemRequest.builder()
                .description("Created by the AWS Systems Manager Java API")
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
