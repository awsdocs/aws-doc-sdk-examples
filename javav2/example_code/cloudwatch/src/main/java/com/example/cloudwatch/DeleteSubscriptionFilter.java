//snippet-sourcedescription:[DeleteSubscriptionFilter.java demonstrates how to delete Amazon CloudWatch log subscription filters.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudWatch]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudwatch;

// snippet-start:[cloudwatch.java2.delete_subscription_filter.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DeleteSubscriptionFilterRequest;
// snippet-end:[cloudwatch.java2.delete_subscription_filter.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteSubscriptionFilter {
    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "  <filter> <logGroup>\n\n" +
            "Where:\n" +
            "  filter - The name of the subscription filter (for example, MyFilter).\n" +
            "  logGroup - The name of the log group. (for example, testgroup).\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String filter = args[0];
        String logGroup = args[1];
        CloudWatchLogsClient logs = CloudWatchLogsClient.builder()
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        deleteSubFilter(logs, filter, logGroup );
        logs.close();
    }
    // snippet-start:[cloudwatch.java2.delete_subscription_filter.main]
    public static void deleteSubFilter(CloudWatchLogsClient logs, String filter, String logGroup) {

        try {
            DeleteSubscriptionFilterRequest request = DeleteSubscriptionFilterRequest.builder()
                .filterName(filter)
                .logGroupName(logGroup)
                .build();

            logs.deleteSubscriptionFilter(request);
            System.out.printf("Successfully deleted CloudWatch logs subscription filter %s", filter);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
   }
   // snippet-end:[cloudwatch.java2.delete_subscription_filter.main]
}

