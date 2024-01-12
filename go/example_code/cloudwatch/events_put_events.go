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

	result, err := svc.PutEvents(&cloudwatchevents.PutEventsInput{
		Entries: []*cloudwatchevents.PutEventsRequestEntry{
			&cloudwatchevents.PutEventsRequestEntry{
				Detail:     aws.String("{ \"key1\": \"value1\", \"key2\": \"value2\" }"),
				DetailType: aws.String("appRequestSubmitted"),
				Resources: []*string{
					aws.String("RESOURCE_ARN"),
				},
				Source: aws.String("com.company.myapp"),
			},
		},
	})
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Ingested events:", result.Entries)
}
