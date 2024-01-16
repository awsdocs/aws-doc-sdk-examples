// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;

/**
 * Lists CloudWatch metrics
 */
public class ListMetrics {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a metric name and metric namespace\n" +
                "Ex: ListMetrics <metric-name> <metric-namespace>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        String namespace = args[1];

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

        ListMetricsRequest request = new ListMetricsRequest()
                .withMetricName(name)
                .withNamespace(namespace);

        boolean done = false;

        while (!done) {
            ListMetricsResult response = cw.listMetrics(request);

            for (Metric metric : response.getMetrics()) {
                System.out.printf(
                        "Retrieved metric %s", metric.getMetricName());
            }

            request.setNextToken(response.getNextToken());

            if (response.getNextToken() == null) {
                done = true;
            }
        }
    }
}
