 
//snippet-sourcedescription:[ListAccessKeys.java demonstrates how to ...]
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
package com.example.iam;
import software.amazon.awssdk.services.iam.model.AccessKeyMetadata;
import software.amazon.awssdk.services.iam.model.ListAccessKeysRequest;
import software.amazon.awssdk.services.iam.model.ListAccessKeysResponse;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

/**
 * List all access keys associated with an IAM user
 */
public class ListAccessKeys {
    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an IAM  username\n" +
            "Ex: ListAccessKeys <username>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        boolean done = false;
        String new_marker = null;

        while (!done) {
            ListAccessKeysResponse response;

        	if(new_marker == null) {
        		 ListAccessKeysRequest request = ListAccessKeysRequest.builder()
        	                .userName(username).build();
                 response = iam.listAccessKeys(request);
        	}
        	else {
        		ListAccessKeysRequest request = ListAccessKeysRequest.builder()
    	                .userName(username)
    	                .marker(new_marker).build();
        		response = iam.listAccessKeys(request);
        	}

            for (AccessKeyMetadata metadata :
                    response.accessKeyMetadata()) {
                System.out.format("Retrieved access key %s",
                        metadata.accessKeyId());
            }

            if (!response.isTruncated()) {
                done = true;
            }
            else {
            	new_marker = response.marker();
            }
        }
    }
}

