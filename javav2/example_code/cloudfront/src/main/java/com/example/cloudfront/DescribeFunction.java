// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.des_function.main]
// snippet-start:[cloudfront.java2.des_function.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.DescribeFunctionRequest;
import software.amazon.awssdk.services.cloudfront.model.FunctionStage;
import software.amazon.awssdk.services.cloudfront.model.DescribeFunctionResponse;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontException;
// snippet-end:[cloudfront.java2.des_function.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeFunction {

    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <functionName>\s

                Where:
                    functionName - The name of the function to describe.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String functionName = args[0];
        CloudFrontClient cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        String eTagVal = describeFunction(cloudFrontClient, functionName);
        System.out.println(eTagVal + " is the eTag value.");
        cloudFrontClient.close();
    }

    public static String describeFunction(CloudFrontClient cloudFrontClient, String functionName) {
        try {
            DescribeFunctionRequest functionRequest = DescribeFunctionRequest.builder()
                    .name(functionName)
                    .stage(FunctionStage.DEVELOPMENT)
                    .build();

            DescribeFunctionResponse functionResponse = cloudFrontClient.describeFunction(functionRequest);
            return functionResponse.eTag();

        } catch (CloudFrontException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
// snippet-end:[cloudfront.java2.des_function.main]
