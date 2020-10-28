//snippet-sourcedescription:[ListIdentityPools.java demonstrates how to list Amazon Cognito identity pools.]
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

//snippet-start:[cognito.java2.listidentitypools.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.IdentityPoolShortDescription;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentityPoolsRequest;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentityPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
//snippet-end:[cognito.java2.listidentitypools.import]

import java.util.List;

public class ListIdentityPools {

    public static void main(String[] args) {

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listIdPools(cognitoclient);
    }

    //snippet-start:[cognito.java2.listidentitypools.main]
    public static void listIdPools(CognitoIdentityClient cognitoclient) {

      try {

            ListIdentityPoolsRequest poolsRequest = ListIdentityPoolsRequest.builder()
                .maxResults(15)
                .build();

            ListIdentityPoolsResponse poolResponse = cognitoclient.listIdentityPools(poolsRequest);
            List<IdentityPoolShortDescription> pools = poolResponse.identityPools();

            for (IdentityPoolShortDescription pool: pools) {
                System.out.println("Pool ID: "+pool.identityPoolId());
                System.out.println("Pool name: "+pool.identityPoolName());
            }

      } catch (CognitoIdentityProviderException e){
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
  }
    //snippet-end:[cognito.java2.listidentitypools.main]
}
