/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package cloudwatch.src.main.java.aws.example.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsRequest;
import com.amazonaws.services.cloudwatch.model.DescribeAlarmsResult;
import com.amazonaws.services.cloudwatch.model.MetricAlarm;

/**
 * Lists all CloudWatch alarms
 */
public class DescribeAlarms {

    public static void main(String[] args) {

        final AmazonCloudWatch cloudWatch = AmazonCloudWatchClientBuilder.defaultClient();

        boolean done = false;

        while(!done) {
            DescribeAlarmsRequest request = new DescribeAlarmsRequest();

            DescribeAlarmsResult response = cloudWatch.describeAlarms(request);

            for(MetricAlarm alarm : response.getMetricAlarms()) {
                System.out.printf("Retrieved alarm %s", alarm.getAlarmName());
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }
}
