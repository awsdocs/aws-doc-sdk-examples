// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[rds.go.create_db_snapshot]
package main

import (
	"fmt"
	"os"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rds"
)

// Creates a RDS snapshot in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//
//	go run s3_create_snapshot INSTANCE_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("Instance name missing!\nUsage: %s instance_name", os.Args[0])
	}

	instance := os.Args[1]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create RDS service client
	svc := rds.New(sess)

	// Get the current date and time to uniquely identify snapshot
	currentTime := time.Now()
	t := currentTime.Format("2006-01-02 15:04:05")
	// Replace space with underscore
	t = strings.Replace(t, " ", "_", -1)

	// Create the RDS snapshot
	_, err = svc.CreateDBSnapshot(&rds.CreateDBSnapshotInput{
		DBInstanceIdentifier: aws.String(instance),
		DBSnapshotIdentifier: aws.String(instance + t),
	})
	if err != nil {
		exitErrorf("Unable to create snapshot in instance %q, %v", instance, err)
	}

	// Wait until snapshot is created before finishing
	fmt.Printf("Waiting for snapshot in instance %q to be created...\n", instance)

	err = svc.WaitUntilDBSnapshotAvailable(&rds.DescribeDBSnapshotsInput{
		DBInstanceIdentifier: aws.String(instance),
	})
	if err != nil {
		exitErrorf("Error occurred while waiting for snapshot to be created in instance, %v", instance)
	}

	fmt.Printf("Snapshot %q successfully created in instance\n", instance)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[rds.go.create_db_snapshot]
