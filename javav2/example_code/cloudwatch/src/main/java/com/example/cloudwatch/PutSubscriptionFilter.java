//snippet-sourcedescription:[PutSubscriptionFilter.java demonstrates how to create an Amazon CloudWatch log subscription filter.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.put_subscription_filter.import]
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
 * In addition, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *
 *   https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class PutSubscriptionFilter {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "  PutSubscriptionFilter <filter> <pattern> <logGroup> <functionArn> \n\n" +
                "Where:\n" +
                "  filter - a filter name (for example, myfilter).\n" +
                "  pattern - a filter pattern (for example, ERROR).\n" +
                "  logGroup - a log group name (testgroup).\n" +
                "  functionArn - an AWS Lambda function ARN (for example, arn:aws:lambda:us-west-2:xxxxxx047983:function:lamda1) .\n" ;

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filter = args[0];
        String pattern = args[1];
        String logGroup = args[2];
        String functionArn = args[3];

        Region region = Region.US_WEST_2;
        CloudWatchLogsClient cwl = CloudWatchLogsClient.builder()
                .region(region)
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
            PutSubscriptionFilterRequest request =
                    PutSubscriptionFilterRequest.builder()
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
}
// snippet-end:[cloudwatch.java2.put_subscription_filter.main]
