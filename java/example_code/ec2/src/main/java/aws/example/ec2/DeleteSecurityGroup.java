// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupResult;

/**
 * Deletes an EC2 security group
 */
public class DeleteSecurityGroup {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a security group id\n" +
                "Ex: DeleteSecurityGroup <security-group-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String group_id = args[0];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest()
                .withGroupId(group_id);

        DeleteSecurityGroupResult response = ec2.deleteSecurityGroup(request);

        System.out.printf(
                "Successfully deleted security group with id %s", group_id);
    }
}
