//snippet-sourcedescription:[DescribeSubscriptionFilters.java demonstrates how to get a list of Amazon CloudWatch subscription filters associated with a log group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.describe_subscription_filters.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeSubscriptionFiltersRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeSubscriptionFiltersResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.SubscriptionFilter;
// snippet-end:[cloudwatch.java2.describe_subscription_filters.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeSubscriptionFilters {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "  <logGroup>\n\n" +
            "Where:\n" +
            "  logGroup - A log group name (for example, myloggroup).\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String logGroup = args[0];
        CloudWatchLogsClient logs = CloudWatchLogsClient.builder()
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeFilters(logs, logGroup);
        logs.close();
    }

    // snippet-start:[cloudwatch.java2.describe_subscription_filters.main]
    public static void describeFilters(CloudWatchLogsClient logs, String logGroup) {

        try {
            boolean done = false;
            String newToken = null;

            while(!done) {
                DescribeSubscriptionFiltersResponse response;
                if (newToken == null) {
                    DescribeSubscriptionFiltersRequest request = DescribeSubscriptionFiltersRequest.builder()
                        .logGroupName(logGroup)
                        .limit(1).build();

                    response = logs.describeSubscriptionFilters(request);
                } else {
                    DescribeSubscriptionFiltersRequest request = DescribeSubscriptionFiltersRequest.builder()
                        .nextToken(newToken)
                        .logGroupName(logGroup)
                        .limit(1).build();
                    response = logs.describeSubscriptionFilters(request);
                }

                for(SubscriptionFilter filter : response.subscriptionFilters()) {
                    System.out.printf("Retrieved filter with name %s, " + "pattern %s " + "and destination arn %s",
                        filter.filterName(),
                        filter.filterPattern(),
                        filter.destinationArn());
                }

                if(response.nextToken() == null) {
                    done = true;
                } else {
                    newToken = response.nextToken();
                }
           }

        } catch (CloudWatchException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
        }
        System.out.printf("Done");
    }
    // snippet-end:[cloudwatch.java2.describe_subscription_filters.main]
}

