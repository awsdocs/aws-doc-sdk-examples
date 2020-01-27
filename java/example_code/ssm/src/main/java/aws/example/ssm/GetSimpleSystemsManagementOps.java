/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetSimpleSystemsManagementOps.java demonstrates how to get information about an OpsItem by using the ID value]
// snippet-service:[ssm]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Simple Systems Management]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-24]
// snippet-sourceauthor:[AWS - scmacdon]

// snippet-start:[ssm.Java1.get_ops.complete]

package aws.example.ssm;

// snippet-start:[ssm.Java1.get_ops.import]
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetOpsItemResult;
import com.amazonaws.services.simplesystemsmanagement.model.OpsItem;
import com.amazonaws.services.simplesystemsmanagement.model.GetOpsItemRequest;
import com.amazonaws.AmazonServiceException;
// snippet-end:[ssm.Java1.get_ops.import]

public class GetSimpleSystemsManagementOps {


    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Please specify a SSM OpsItem ID value. You can obtain this value using the AWS Console.");
            System.exit(1);
        }

        // snippet-start:[ssm.Java1.get_ops.main]

        // Get the OpsItem ID value
        String opsID = args[0];

        // Create the AWSSimpleSystemsManagement client object
        AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

        try {
            GetOpsItemRequest opsRequest = new GetOpsItemRequest();
            opsRequest.setOpsItemId("oi-3dc4b67a9f2f");

            GetOpsItemResult opsResults = ssm.getOpsItem(opsRequest);

            OpsItem item = opsResults.getOpsItem();

            System.out.println(item.getTitle());
            System.out.println(item.getDescription());
            System.out.println(item.getSource());

        } catch (AmazonServiceException e) {
            e.getStackTrace();
        }
        // snippet-end:[ssm.Java1.get_ops.main]
    }
}
// snippet-end:[ssm.Java1.get_ops.complete]
