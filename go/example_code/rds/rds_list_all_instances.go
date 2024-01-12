// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[rds.go.describe_db_instances]
package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rds"
)

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create RDS service client
	svc := rds.New(sess)

	result, err := svc.DescribeDBInstances(nil)
	if err != nil {
		exitErrorf("Unable to list instances, %v", err)
	}

	fmt.Println("Instances:")

	for _, d := range result.DBInstances {
		fmt.Printf("* %s created on %s\n",
			aws.StringValue(d.DBInstanceIdentifier), aws.TimeValue(d.InstanceCreateTime))
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[rds.go.describe_db_instances]
