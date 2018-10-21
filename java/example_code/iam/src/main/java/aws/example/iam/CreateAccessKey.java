 
//snippet-sourcedescription:[CreateAccessKey.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
package aws.example.iam;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;

/**
 * Creates an access key for an IAM user
 */
public class CreateAccessKey {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an IAM user\n" +
            "Ex: CreateAccessKey <user>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String user = args[0];

        final AmazonIdentityManagement iam =
            AmazonIdentityManagementClientBuilder.defaultClient();

        CreateAccessKeyRequest request = new CreateAccessKeyRequest()
            .withUserName(user);

        CreateAccessKeyResult response = iam.createAccessKey(request);

        System.out.println("Created access key: " + response.getAccessKey());
    }
}

