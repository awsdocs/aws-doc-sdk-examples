 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Cloudwatch]
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
package aws.example.cloudwatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;

/**
 * Creates a new CloudWatch alarm based on CPU utilization for an instance
 */
public class PutMetricAlarm {
    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply an alarm name and instance id\n" +
            "Ex: DeleteAlarm <alarm-name> <instance-id>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alarmName = args[0];
        String instanceId = args[1];

        final AmazonCloudWatch cw =
            AmazonCloudWatchClientBuilder.defaultClient();

        Dimension dimension = new Dimension()
            .withName("InstanceId")
            .withValue(instanceId);

        PutMetricAlarmRequest request = new PutMetricAlarmRequest()
            .withAlarmName(alarmName)
            .withComparisonOperator(
                ComparisonOperator.GreaterThanThreshold)
            .withEvaluationPeriods(1)
            .withMetricName("CPUUtilization")
            .withNamespace("AWS/EC2")
            .withPeriod(60)
            .withStatistic(Statistic.Average)
            .withThreshold(70.0)
            .withActionsEnabled(false)
            .withAlarmDescription(
                "Alarm when server CPU utilization exceeds 70%")
            .withUnit(StandardUnit.Seconds)
            .withDimensions(dimension);

        PutMetricAlarmResult response = cw.putMetricAlarm(request);

        System.out.printf(
            "Successfully created alarm with name %s", alarmName);

    }
}
