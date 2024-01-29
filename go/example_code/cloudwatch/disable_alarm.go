// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"

	"fmt"
	"os"
)

func main() {
	if len(os.Args) != 2 {
		fmt.Println("You must supply an alarm name")
		os.Exit(1)
	}

	name := os.Args[1]

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create new CloudWatch client.
	svc := cloudwatch.New(sess)

	// Disable the alarm.
	_, err := svc.DisableAlarmActions(&cloudwatch.DisableAlarmActionsInput{
		AlarmNames: []*string{
			aws.String(name),
		},
	})
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success")
}
