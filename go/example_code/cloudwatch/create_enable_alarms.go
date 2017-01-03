/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"
)

// Usage:
// go run main.go <customer id> <instance id> <alarm name>
func main() {
	// Load session from shared config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create new cloudwatch client.
	svc := cloudwatch.New(sess)

	// Create a metric alarm that will reboot an instance if its CPU utilization is
	// greate than 70.0%.
	_, err := svc.PutMetricAlarm(&cloudwatch.PutMetricAlarmInput{
		AlarmName:          &os.Args[3],
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
			aws.String(fmt.Sprintf("arn:aws:swf:us-east-1:%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", os.Args[1])),
		},
		Dimensions: []*cloudwatch.Dimension{
			&cloudwatch.Dimension{
				Name:  aws.String("InstanceId"),
				Value: &os.Args[2],
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
			&os.Args[3],
		},
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Alarm action enabled", result)
}
