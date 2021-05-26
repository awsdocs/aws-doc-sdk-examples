//snippet-sourcedescription:[DeleteFunction.java demonstrates how to delete a CloudFront function.]
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
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
import software.amazon.awssdk.services.cloudfront.model.DeleteFunctionRequest;

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteFunction {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <functionName> <ifMatchVal>\n\n" +
                "Where:\n" +
                "    functionName - the name of the function to delete. \n"+
                "    ifMatchVal - The current version (ETag value) of the function that you are deleting, which you can get using DescribeFunction. \n" ;

        if (args.length != 2) {
             System.out.println(USAGE);
             System.exit(1);
        }

        String functionName = args[0];
        String ifMatchVal = args[1];
        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        deleteSpecificFunction(cloudFrontClient, functionName, ifMatchVal);
        cloudFrontClient.close();
    }

    public static void deleteSpecificFunction( CloudFrontClient cloudFrontClient, String functionName,String ifMatchVal){

        try {
            DeleteFunctionRequest functionRequest = DeleteFunctionRequest.builder()
                .name(functionName)
                .ifMatch(ifMatchVal)
                .build();

            cloudFrontClient.deleteFunction(functionRequest);
            System.out.println(functionName +" was successfully deleted.");

        } catch (CloudFrontException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}