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
	// Must have one arg, the name of the alarm
	if len(os.Args) != 2 {
		fmt.Println("You must supply an alarm name")
		os.Exit(1)
	}

	alarmName := os.Args[1]

	sess, err := session.NewSession()
	if err != nil {
		fmt.Println("failed to create session,", err)
		os.Exit(1)
	}

	svc := cloudwatch.New(sess, &aws.Config{Region: aws.String("us-west-2")})

	params := &cloudwatch.DescribeAlarmHistoryInput{
		AlarmName: aws.String(alarmName),
	}

	resp, err := svc.DescribeAlarmHistory(params)
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

	fmt.Println(resp)
}
