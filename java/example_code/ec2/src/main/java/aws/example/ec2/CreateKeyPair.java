/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package aws.example.ec2;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;

/**
 * Creates an EC2 key pair
 */
public class CreateKeyPair
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a key pair name\n" +
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
            "Successfulyl created key pair named %s",
            key_name);
    }
}

