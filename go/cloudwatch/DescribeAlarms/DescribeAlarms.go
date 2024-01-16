// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[cloudwatch.go.describe_alarms]
package main

// snippet-start:[cloudwatch.go.describe_alarms.imports]
import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"
)

// snippet-end:[cloudwatch.go.describe_alarms.imports]

// ListAlarms returns a list of your Amazon CloudWatch alarms
// Inputs:
//
//	sess is the current session, which provides configuration for the SDK's service clients
//
// Output:
//
//	If success, the list of alarms and nil
//	Otherwise, nil and an error from the call to DescribeAlarms
func ListAlarms(sess *session.Session) (*cloudwatch.DescribeAlarmsOutput, error) {
	// Create new service client
	// snippet-start:[cloudwatch.go.describe_alarms.call]
	svc := cloudwatch.New(sess)

	resp, err := svc.DescribeAlarms(nil)
	// snippet-end:[cloudwatch.go.describe_alarms.call]

	if err != nil {
		return nil, err
	}

	return resp, nil
}

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file (~/.aws/credentials)
	// snippet-start:[cloudwatch.go.describe_alarms.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[cloudwatch.go.describe_alarms.session]

	resp, err := ListAlarms(sess)
	if err != nil {
		fmt.Println("Got an error listing alarms:")
		fmt.Println(err)
		return
	}

	// snippet-start:[cloudwatch.go.describe_alarms.display]
	fmt.Println("Alarms:")
	for _, alarm := range resp.MetricAlarms {
		fmt.Println("    " + *alarm.AlarmName)
	}
	// snippet-end:[cloudwatch.go.describe_alarms.display]
}

// snippet-end:[cloudwatch.go.describe_alarms]
