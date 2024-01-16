// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairResult;

/**
 * Deletes a key pair.
 */
public class DeleteKeyPair {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a key pair name\n" +
                "Ex: DeleteKeyPair <key-pair-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String key_name = args[0];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DeleteKeyPairRequest request = new DeleteKeyPairRequest()
                .withKeyName(key_name);

        DeleteKeyPairResult response = ec2.deleteKeyPair(request);

        System.out.printf(
                "Successfully deleted key pair named %s", key_name);
    }
}
