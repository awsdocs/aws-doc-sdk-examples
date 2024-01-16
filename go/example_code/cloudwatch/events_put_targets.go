// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatchevents"

	"fmt"
)

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create the cloudwatch events client
	svc := cloudwatchevents.New(sess)

	result, err := svc.PutTargets(&cloudwatchevents.PutTargetsInput{
		Rule: aws.String("DEMO_EVENT"),
		Targets: []*cloudwatchevents.Target{
			&cloudwatchevents.Target{
				Arn: aws.String("LAMBDA_FUNCTION_ARN"),
				Id:  aws.String("myCloudWatchEventsTarget"),
			},
		},
	})
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
