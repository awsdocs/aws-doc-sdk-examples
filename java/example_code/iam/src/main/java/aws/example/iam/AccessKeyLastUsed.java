 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
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
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedRequest;
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedResult;

/**
 * Displays the time that an access key was last used
 */
public class AccessKeyLastUsed {
    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an access key id\n" +
            "Ex: AccessKeyLastUsed <access-key-id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String access_id = args[0];

        final AmazonIdentityManagement iam =
            AmazonIdentityManagementClientBuilder.defaultClient();

        GetAccessKeyLastUsedRequest request = new GetAccessKeyLastUsedRequest()
            .withAccessKeyId(access_id);

        GetAccessKeyLastUsedResult response = iam.getAccessKeyLastUsed(request);

        System.out.println("Access key was last used at: " +
                response.getAccessKeyLastUsed().getLastUsedDate());
    }
}

