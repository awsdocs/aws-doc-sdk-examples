//snippet-sourcedescription:[DescribeFunction.java demonstrates how to get configuration information and metadata about a CloudFront function.]
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
import software.amazon.awssdk.services.cloudfront.model.DescribeFunctionRequest;
import software.amazon.awssdk.services.cloudfront.model.FunctionStage;
import software.amazon.awssdk.services.cloudfront.model.DescribeFunctionResponse;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeFunction {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <functionName> \n\n" +
                "Where:\n" +
                "    functionName - the name of the function to delete. \n";

         if (args.length != 1) {
             System.out.println(USAGE);
             System.exit(1);
        }

        String functionName = args[0];
        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        String eTagVal = describeSinFunction(cloudFrontClient, functionName);
        System.out.println(eTagVal +" is the eTag value.");
        cloudFrontClient.close();
    }

    public static String describeSinFunction(CloudFrontClient cloudFrontClient, String functionName) {

        try {

            DescribeFunctionRequest functionRequest = DescribeFunctionRequest.builder()
                    .name(functionName)
                    .stage(FunctionStage.DEVELOPMENT)
                    .build();

            DescribeFunctionResponse functionResponse = cloudFrontClient.describeFunction(functionRequest);
            return functionResponse.eTag();

        } catch (CloudFrontException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
