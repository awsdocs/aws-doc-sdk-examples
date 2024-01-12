// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"

	"fmt"
	"os"
)

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := cloudwatch.New(sess)

	resp, err := svc.DescribeAlarms(nil)
	if err != nil {
		fmt.Println("Got error getting alarm descriptions:")
		fmt.Println(err.Error())
		os.Exit(1)
	}

	for _, alarm := range resp.MetricAlarms {
		fmt.Println(*alarm.AlarmName)
	}
}
