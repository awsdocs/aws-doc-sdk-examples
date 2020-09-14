// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ResolveOpsItem.java demonstrates how to resolve an OpsItem.]
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

package com.example.ssm;

// snippet-start:[ssm.Java2.resolve_ops.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SsmException;
import software.amazon.awssdk.services.ssm.model.UpdateOpsItemRequest;
import software.amazon.awssdk.services.ssm.model.OpsItemStatus;
// snippet-end:[ssm.Java2.resolve_ops.import]

public class ResolveOpsItem {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ResolveOpsItem <opsID>\n\n" +
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