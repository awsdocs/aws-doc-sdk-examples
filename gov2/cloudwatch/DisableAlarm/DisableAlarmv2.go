// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[cloudwatch.go-v2.DisableAlarm]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
)

// CWDisableAlarmAPI defines the interface for the DisableAlarmActions function.
// We use this interface to test the function using a mocked service.
type CWDisableAlarmAPI interface {
	DisableAlarmActions(ctx context.Context,
		params *cloudwatch.DisableAlarmActionsInput,
		optFns ...func(*cloudwatch.Options)) (*cloudwatch.DisableAlarmActionsOutput, error)
}

// DisableAlarm disables an Amazon CloudWatch alarm
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a DisableAlarmActionsOutput object containing the result of the service call and nil
//     Otherwise, nil and the error from the call to DisableAlarmActions
func DisableAlarm(c context.Context, api CWDisableAlarmAPI, input *cloudwatch.DisableAlarmActionsInput) (*cloudwatch.DisableAlarmActionsOutput, error) {
	return api.DisableAlarmActions(c, input)
}

func main() {
	alarmName := flag.String("a", "", "The name of the alarm to disable")
	flag.Parse()

	if *alarmName == "" {
		fmt.Println("You must supply an alarm name to disable")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatch.NewFromConfig(cfg)

	input := &cloudwatch.DisableAlarmActionsInput{
		AlarmNames: []*string{
			alarmName,
		},
	}

	_, err = DisableAlarm(context.Background(), client, input)
	if err != nil {
		fmt.Println("Could not disable alarm " + *alarmName)
	} else {
		fmt.Println("Disabled alarm " + *alarmName)
	}
}

// snippet-end:[cloudwatch.go-v2.DisableAlarm]
