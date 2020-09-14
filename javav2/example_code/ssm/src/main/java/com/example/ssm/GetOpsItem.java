// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetSimpleSystemsManagementOps.java demonstrates how to get information about an OpsItem by using the ID value]
// snippet-service:[ssm]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Simple Systems Management]
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
                "    opsID - the Ops item ID value\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String opsID = args[0];

        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
                .region(region)
                .build();

        getOpsItem(ssmClient, opsID );
    }

    // snippet-start:[ssm.Java2.get_ops.main]
    public static void getOpsItem(SsmClient ssmClient, String opsID ) {

        try {
            // Create a DescribeParametersRequest object
            GetOpsItemRequest opsRequest = GetOpsItemRequest.builder()
                    .opsItemId(opsID)
                    .build();

            // Get SSM Parameters (you can define them in the AWS Console)
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

