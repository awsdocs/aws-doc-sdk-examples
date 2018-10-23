//snippet-sourcedescription:[ListMetrics.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
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
package com.example.cloudwatch;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;

/**
 * Lists CloudWatch metrics
 */
public class ListMetrics {

    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply a metric namespace\n" +
            "Ex: ListMetrics <metric-namespace>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String namespace = args[0];

        CloudWatchClient cw =
        		CloudWatchClient.builder().build();

        boolean done = false;
        String next_token = null;

        while(!done) {
        	
        	ListMetricsResponse response;
        	
        	if (next_token == null) {
        		ListMetricsRequest request = ListMetricsRequest.builder()
        				.namespace(namespace)
        				.build();

        		response = cw.listMetrics(request);
        	}
        	else {
        		ListMetricsRequest request = ListMetricsRequest.builder()
                        .namespace(namespace)
                        .nextToken(next_token)
                        .build();
        		
        		response = cw.listMetrics(request);
        	}

            for(Metric metric : response.metrics()) {
                System.out.printf(
                    "Retrieved metric %s", metric.metricName());
                System.out.println();
            }

            if(response.nextToken() == null) {
                done = true;
            }
            else {
            	next_token = response.nextToken();
            }
        }
    }
}

