// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;

/**
 * Creates an EC2 key pair
 */
public class CreateKeyPair {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a key pair name\n" +
                "Ex: CreateKeyPair <key-pair-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String key_name = args[0];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        CreateKeyPairRequest request = new CreateKeyPairRequest()
                .withKeyName(key_name);

        CreateKeyPairResult response = ec2.createKeyPair(request);

        System.out.printf(
                "Successfully created key pair named %s",
                key_name);
    }
}
