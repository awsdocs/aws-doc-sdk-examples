//snippet-sourcedescription:[ListFunctions.java demonstrates how to list CloudFront functions.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon CloudFront]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2021]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.list.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;
import java.util.List;
// snippet-end:[cloudfront.java2.list.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListFunctions {

    public static void main(String[] args) {

        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listAllFunctions(cloudFrontClient);
        cloudFrontClient.close();

    }

    // snippet-start:[cloudfront.java2.list.main]
    public static void listAllFunctions( CloudFrontClient cloudFrontClient) {

       try {
            ListFunctionsRequest functionsRequest = ListFunctionsRequest.builder()
                .stage(FunctionStage.DEVELOPMENT)
                .maxItems("10")
                .build();

            ListFunctionsResponse response = cloudFrontClient.listFunctions(functionsRequest);
            FunctionList allFunctions = response.functionList();
            List<FunctionSummary> functions = allFunctions.items();
            for (FunctionSummary funSummary: functions) {
                System.out.println("Function name is "+funSummary.name());
                System.out.println("Function runtime is "+funSummary.functionConfig().runtime().toString());
            }

       } catch (CloudFrontException e){
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
    }
    // snippet-end:[cloudfront.java2.list.main]
}