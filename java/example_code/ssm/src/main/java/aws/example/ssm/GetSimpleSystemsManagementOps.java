// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
            System.out
                    .println("Please specify a SSM OpsItem ID value. You can obtain this value using the AWS Console.");
            System.exit(1);
        }

        // snippet-start:[ssm.Java1.get_ops.main]

        // Get the OpsItem ID value
        String opsID = args[0];

        // Create the AWSSimpleSystemsManagement client object
        AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(Regions.DEFAULT_REGION).build();

        try {
            GetOpsItemRequest opsRequest = new GetOpsItemRequest();
            opsRequest.setOpsItemId(opsID);

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
