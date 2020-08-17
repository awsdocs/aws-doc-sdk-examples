//snippet-sourcedescription:[DeleteIdentityPool.java demonstrates how to delete an existing Amazon Cognito identity pool.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/14/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.example.cognito;

//snippet-start:[cognito.java2.deleteidpool.import]
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.DeleteIdentityPoolRequest;
//snippet-end:[cognito.java2.deleteidpool.import]

public class DeleteIdentityPool {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteIdentityPool <identity_pool_id> \n\n" +
                "Where:\n" +
                "    identityPoolId  - The AWS Region and GUID of your identity pool.\n\n" ;

        String identityPoold = args[0];

        CognitoIdentityClient cognitoIdclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deleteIdPool(cognitoIdclient, identityPoold);
    }

    //snippet-start:[cognito.java2.deleteidpool.main]
    public static void deleteIdPool(CognitoIdentityClient cognitoIdclient, String identityPoold) {
        try {

        DeleteIdentityPoolRequest identityPoolRequest = DeleteIdentityPoolRequest.builder()
                .identityPoolId(identityPoold)
                .build();

        cognitoIdclient.deleteIdentityPool(identityPoolRequest);
        System.out.println("Done");

        } catch (AwsServiceException e){
         System.err.println(e.awsErrorDetails().errorMessage());
         System.exit(1);
     }
  }
    //snippet-end:[cognito.java2.deleteidpool.main]
}
