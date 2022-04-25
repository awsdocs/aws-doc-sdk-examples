// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[cloudwatch.go-v2.CreateEnableMetricAlarm]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

// CWEnableAlarmAPI defines the interface for the PutMetricAlarm function.
// We use this interface to test the function using a mocked service.
type CWEnableAlarmAPI interface {
	PutMetricAlarm(ctx context.Context,
		params *cloudwatch.PutMetricAlarmInput,
		optFns ...func(*cloudwatch.Options)) (*cloudwatch.PutMetricAlarmOutput, error)
	EnableAlarmActions(ctx context.Context,
		params *cloudwatch.EnableAlarmActionsInput,
		optFns ...func(*cloudwatch.Options)) (*cloudwatch.EnableAlarmActionsOutput, error)
}

// CreateMetricAlarm creates a metric alarm
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a PutMetricAlarmOutput object containing the result of the service call and nil
//     Otherwise, the error from a call to PutMetricAlarm
func CreateMetricAlarm(c context.Context, api CWEnableAlarmAPI, input *cloudwatch.PutMetricAlarmInput) (*cloudwatch.PutMetricAlarmOutput, error) {
	return api.PutMetricAlarm(c, input)
}

// EnableAlarm enables the specified Amazon CloudWatch alarm
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a EnableAlarmActionsOutput object containing the result of the service call and nil
//     Otherwise, the error from a call to PutMetricAlarm
func EnableAlarm(c context.Context, api CWEnableAlarmAPI, input *cloudwatch.EnableAlarmActionsInput) (*cloudwatch.EnableAlarmActionsOutput, error) {
	return api.EnableAlarmActions(c, input)
}

func main() {
	instanceName := flag.String("n", "", "The instance name")
	instanceID := flag.String("i", "", "The instance ID")
	alarmName := flag.String("a", "", "The alarm name")
	flag.Parse()

	if *instanceName == "" || *instanceID == "" || *alarmName == "" {
		fmt.Println("You must supply an instance name, instance ID, and alarm name")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatch.NewFromConfig(cfg)

	putInput := &cloudwatch.PutMetricAlarmInput{
		AlarmName:          alarmName,
		ComparisonOperator: types.ComparisonOperatorGreaterThanOrEqualToThreshold,
		EvaluationPeriods:  aws.Int32(1),
		MetricName:         aws.String("CPUUtilization"),
		Namespace:          aws.String("AWS/EC2"),
		Period:             aws.Int32(60),
		Statistic:          types.StatisticAverage,
		Threshold:          aws.Float64(70.0),
		ActionsEnabled:     aws.Bool(true),
		AlarmDescription:   aws.String("Alarm when server CPU exceeds 70%"),
		Unit:               types.StandardUnitSeconds,
		AlarmActions: []string{
			fmt.Sprintf("arn:aws:swf:"+cfg.Region+":%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", instanceName),
		},
		Dimensions: []types.Dimension{
			{
				Name:  aws.String("InstanceId"),
				Value: instanceID,
			},
		},
	}

	_, err = CreateMetricAlarm(context.TODO(), client, putInput)
	if err != nil {
		fmt.Println(err)
		return
	}

	enableInput := &cloudwatch.EnableAlarmActionsInput{
		AlarmNames: []string{
			*instanceID,
		},
	}

	_, err = EnableAlarm(context.TODO(), client, enableInput)
	if err != nil {
		fmt.Println(err)
		return
	}

	fmt.Println("Enabled alarm " + *alarmName + " for EC2 instance " + *instanceName)
}

// snippet-end:[cloudwatch.go-v2.CreateEnableMetricAlarm]
