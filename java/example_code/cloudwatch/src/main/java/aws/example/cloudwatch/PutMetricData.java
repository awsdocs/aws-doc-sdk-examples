// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

/**
 * Puts a sample metric data point
 */
public class PutMetricData {
        public static void main(String[] args) {

                final String USAGE = "To run this example, supply a data point:\n" +
                                "Ex: PutMetricData <data_point>\n";

                if (args.length != 1) {
                        System.out.println(USAGE);
                        System.exit(1);
                }

                Double data_point = Double.parseDouble(args[0]);

                final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

                Dimension dimension = new Dimension()
                                .withName("UNIQUE_PAGES")
                                .withValue("URLS");

                MetricDatum datum = new MetricDatum()
                                .withMetricName("PAGES_VISITED")
                                .withUnit(StandardUnit.None)
                                .withValue(data_point)
                                .withDimensions(dimension);

                PutMetricDataRequest request = new PutMetricDataRequest()
                                .withNamespace("SITE/TRAFFIC")
                                .withMetricData(datum);

                PutMetricDataResult response = cw.putMetricData(request);

                System.out.printf("Successfully put data point %f", data_point);
        }
}
