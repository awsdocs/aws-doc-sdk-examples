// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
//snippet-start:[cloudwatch.go-v2.DescribeAlarms]
package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
)

// CWDescribeAlarmsAPI defines the interface for the DescribeAlarms function.
// We use this interface to test the function using a mocked service.
type CWDescribeAlarmsAPI interface {
	DescribeAlarms(ctx context.Context,
		params *cloudwatch.DescribeAlarmsInput,
		optFns ...func(*cloudwatch.Options)) (*cloudwatch.DescribeAlarmsOutput, error)
}

// ListAlarms returns a list of your Amazon CloudWatch alarms
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a DescribeAlarmsOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to DescribeAlarms
func ListAlarms(c context.Context, api CWDescribeAlarmsAPI, input *cloudwatch.DescribeAlarmsInput) (*cloudwatch.DescribeAlarmsOutput, error) {
	return api.DescribeAlarms(c, input)
}

func main() {
	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatch.NewFromConfig(cfg)

	input := &cloudwatch.DescribeAlarmsInput{}

	resp, err := ListAlarms(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error listing alarms:")
		fmt.Println(err)
		return
	}

	fmt.Println("Composite alarms:")
	for _, alarm := range resp.CompositeAlarms {
		fmt.Println("    " + *alarm.AlarmName)
	}

	fmt.Println("Metric alarms:")
	for _, alarm := range resp.MetricAlarms {
		fmt.Println("    " + *alarm.AlarmName)
	}
}

//snippet-end:[cloudwatch.go-v2.DescribeAlarms]
