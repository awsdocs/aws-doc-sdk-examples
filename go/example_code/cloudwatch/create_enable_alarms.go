//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Creates and enables an action on an alarm.]
//snippet-keyword:[AWS CloudWatch]
//snippet-keyword:[EnableAlarmActions function]
//snippet-keyword:[PutMetricAlarm function]
//snippet-keyword:[Go]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"

    "fmt"
    "os"
)

func main() {
    if len(os.Args) != 4 {
        fmt.Println("You must supply an instance name, value, and alarm name")
        os.Exit(1)
    }

    instance := os.Args[1]
    value := os.Args[2]
    name := os.Args[3]
    
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new CloudWatch client.
    svc := cloudwatch.New(sess)

    // Create a metric alarm that reboots an instance if its CPU utilization is
    // greater than 70.0%.
    _, err := svc.PutMetricAlarm(&cloudwatch.PutMetricAlarmInput{
        AlarmName:          aws.String(name),
        ComparisonOperator: aws.String(cloudwatch.ComparisonOperatorGreaterThanThreshold),
        EvaluationPeriods:  aws.Int64(1),
        MetricName:         aws.String("CPUUtilization"),
        Namespace:          aws.String("AWS/EC2"),
        Period:             aws.Int64(60),
        Statistic:          aws.String(cloudwatch.StatisticAverage),
        Threshold:          aws.Float64(70.0),
        ActionsEnabled:     aws.Bool(true),
        AlarmDescription:   aws.String("Alarm when server CPU exceeds 70%"),
        Unit:               aws.String(cloudwatch.StandardUnitSeconds),

        // This is apart of the default workflow actions. This one will reboot the instance, if the
        // alarm is triggered.
        AlarmActions: []*string{
            aws.String(fmt.Sprintf("arn:aws:swf:us-east-1:%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", instance)),
        },
        Dimensions: []*cloudwatch.Dimension{
            {
                Name:  aws.String("InstanceId"),
                Value: aws.String(value),
            },
        },
    })
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    // This will enable the alarm to our instance.
    result, err := svc.EnableAlarmActions(&cloudwatch.EnableAlarmActionsInput{
        AlarmNames: []*string{
            aws.String(name),
        },
    })
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Alarm action enabled", result)
}
