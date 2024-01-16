// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.PutSubscriptionFilterRequest;
import com.amazonaws.services.logs.model.PutSubscriptionFilterResult;

/**
 * Creates a CloudWatch Logs subscription filter.
 */
public class PutSubscriptionFilter {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply:\n" +
                "* a filter name\n" +
                "* filter pattern\n" +
                "* log group name\n" +
                "* lambda function arn\n\n" +
                "Ex: PutSubscriptionFilter <filter-name> \\\n" +
                "                          <filter pattern> \\\n" +
                "                          <log-group-name> \\\n" +
                "                          <lambda-function-arn>\n";

        if (args.length != 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String filter = args[0];
        String pattern = args[1];
        String log_group = args[2];
        String function_arn = args[3];

        final AWSLogs cwl = AWSLogsClientBuilder.defaultClient();

        PutSubscriptionFilterRequest request = new PutSubscriptionFilterRequest()
                .withFilterName(filter)
                .withFilterPattern(pattern)
                .withLogGroupName(log_group)
                .withDestinationArn(function_arn);

        PutSubscriptionFilterResult response = cwl.putSubscriptionFilter(request);

        System.out.printf(
                "Successfully created CloudWatch logs subscription filter %s",
                filter);
    }
}
