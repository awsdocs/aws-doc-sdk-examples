//snippet-sourcedescription:[PutSubscriptionFilter.java demonstrates how to create an Amazon CloudWatch log subscription filter.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_subscription_filter.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutSubscriptionFilterRequest;
// snippet-end:[cloudwatch.java2.put_subscription_filter.import]

/**
 * Before running this code example, you need to grant permission to CloudWatch Logs the right to execute your Lambda function.
 * To perform this task, you can use this CLI command:
 *
 * aws lambda add-permission --function-name "lamda1" --statement-id "lamda1"
 * --principal "logs.us-west-2.amazonaws.com" --action "lambda:InvokeFunction"
 * --source-arn "arn:aws:logs:us-west-2:xxxxxx047983:log-group:testgroup:*"
 * --source-account "xxxxxx047983"
 *
 * Make sure you replace the function name with your function name and replace the xxxxxx with your account details.
 * For more information, see "Subscription Filters with AWS Lambda" in the Amazon CloudWatch Logs Guide.
 *
 *
 * Also, before running this Java V2 code example,set up your development environment,including your credentials.
 *
 * For more information,see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class PutSubscriptionFilter {
    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "  <filter> <pattern> <logGroup> <functionArn> \n\n" +
            "Where:\n" +
            "  filter - A filter name (for example, myfilter).\n" +
            "  pattern - A filter pattern (for example, ERROR).\n" +
            "  logGroup - A log group name (testgroup).\n" +
            "  functionArn - An AWS Lambda function ARN (for example, arn:aws:lambda:us-west-2:xxxxxx047983:function:lamda1) .\n" ;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String filter = args[0];
        String pattern = args[1];
        String logGroup = args[2];
        String functionArn = args[3];
        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cwl = CloudWatchLogsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        putSubFilters(cwl, filter, pattern, logGroup, functionArn ) ;
        cwl.close();
    }

    // snippet-start:[cloudwatch.java2.put_subscription_filter.main]
    public static void putSubFilters(CloudWatchLogsClient cwl,
                                     String filter,
                                     String pattern,
                                     String logGroup,
                                     String functionArn) {

        try {
            PutSubscriptionFilterRequest request = PutSubscriptionFilterRequest.builder()
                .filterName(filter)
                .filterPattern(pattern)
                .logGroupName(logGroup)
                .destinationArn(functionArn)
                .build();

            cwl.putSubscriptionFilter(request);
            System.out.printf(
                    "Successfully created CloudWatch logs subscription filter %s",
                    filter);

        } catch (CloudWatchLogsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[cloudwatch.java2.put_subscription_filter.main]
}

