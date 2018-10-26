//snippet-sourcedescription:[ListMetrics.java demonstrates how to list CloudWatch metrics.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cloudwatch]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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

        final String USAGE =
            "To run this example, supply a metric name and metric namespace\n" +
            "Ex: ListMetrics <metric-name> <metric-namespace>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        String namespace = args[1];

        final AmazonCloudWatch cw =
            AmazonCloudWatchClientBuilder.defaultClient();

        ListMetricsRequest request = new ListMetricsRequest()
                .withMetricName(name)
                .withNamespace(namespace);

        boolean done = false;

        while(!done) {
            ListMetricsResult response = cw.listMetrics(request);

            for(Metric metric : response.getMetrics()) {
                System.out.printf(
                    "Retrieved metric %s", metric.getMetricName());
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }
}
