// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DescribeSubscriptionFiltersRequest;
import com.amazonaws.services.logs.model.DescribeSubscriptionFiltersResult;
import com.amazonaws.services.logs.model.SubscriptionFilter;

/**
 * Lists CloudWatch subscription filters associated with a log group.
 */
public class DescribeSubscriptionFilters {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a log group name\n" +
                "Ex: DescribeSubscriptionFilters <log-group-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String log_group = args[0];

        final AWSLogs logs = AWSLogsClientBuilder.defaultClient();
        boolean done = false;

        DescribeSubscriptionFiltersRequest request = new DescribeSubscriptionFiltersRequest()
                .withLogGroupName(log_group)
                .withLimit(1);

        while (!done) {

            DescribeSubscriptionFiltersResult response = logs.describeSubscriptionFilters(request);

            for (SubscriptionFilter filter : response.getSubscriptionFilters()) {
                System.out.printf(
                        "Retrieved filter with name %s, " +
                                "pattern %s " +
                                "and destination arn %s",
                        filter.getFilterName(),
                        filter.getFilterPattern(),
                        filter.getDestinationArn());
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }
    }
}
