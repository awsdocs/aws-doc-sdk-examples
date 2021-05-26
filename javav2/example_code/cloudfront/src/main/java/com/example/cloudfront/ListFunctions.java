//snippet-sourcedescription:[ListFunctions.java demonstrates how to list CloudFront functions.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon CloudFront]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/20/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.cloudfront;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

import java.util.List;

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListFunctions {

    public static void main(String[] args) {

        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        listAllFunctions(cloudFrontClient);
        cloudFrontClient.close();

    }

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
}