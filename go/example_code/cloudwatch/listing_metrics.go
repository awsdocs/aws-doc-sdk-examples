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
	if len(os.Args) != 4 {
		fmt.Println("You must supply a metric name, namespace, and dimension name")
		os.Exit(1)
	}

	metric := os.Args[1]
	namespace := os.Args[2]
	dimension := os.Args[3]

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create CloudWatch client
	svc := cloudwatch.New(sess)

	// Get the list of metrics matching your criteria
	result, err := svc.ListMetrics(&cloudwatch.ListMetricsInput{
		MetricName: aws.String(metric),
		Namespace:  aws.String(namespace),
		Dimensions: []*cloudwatch.DimensionFilter{
			&cloudwatch.DimensionFilter{
				Name: aws.String(dimension),
			},
		},
	})
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Metrics", result.Metrics)
}
