// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	//go get -u github.com/aws/aws-sdk-go
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ses"

	"fmt"
)

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create an SES session.
	svc := ses.New(sess)

	// Attempt to send the email.
	result, err := svc.GetSendStatistics(nil)

	// Display any error message
	if err != nil {
		fmt.Println(err.Error())
		return
	}

	dps := result.SendDataPoints

	fmt.Println("Got", len(dps), "datapoints")
	fmt.Println("")

	for _, dp := range dps {
		fmt.Println("Timestamp: ", dp.Timestamp)
		fmt.Println("Attempts:  ", aws.Int64Value(dp.DeliveryAttempts))
		fmt.Println("Bounces:   ", aws.Int64Value(dp.Bounces))
		fmt.Println("Complaints:", aws.Int64Value(dp.Complaints))
		fmt.Println("Rejects:   ", aws.Int64Value(dp.Rejects))
		fmt.Println("")
	}
}
