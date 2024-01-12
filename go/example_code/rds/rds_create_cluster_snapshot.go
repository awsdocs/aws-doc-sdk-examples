// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[rds.go.create_cluster_snapshot]
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

// Creates a RDS Cluster snapshot in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//
//	go run rds_create_cluster_snapshot CLUSTER_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("Cluster name missing!\nUsage: %s cluster_name", os.Args[0])
	}

	cluster := os.Args[1]

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

	// Create the RDS Cluster snapshot
	_, err = svc.CreateDBClusterSnapshot(&rds.CreateDBClusterSnapshotInput{
		DBClusterIdentifier:         aws.String(cluster),
		DBClusterSnapshotIdentifier: aws.String(cluster + t),
	})
	if err != nil {
		exitErrorf("Unable to create snapshot in cluster %q, %v", cluster, err)
	}

	// Wait until snapshot is created before finishing
	fmt.Printf("Waiting for snapshot in cluster %q to be created...\n", cluster)

	err = svc.WaitUntilDBSnapshotAvailable(&rds.DescribeDBSnapshotsInput{
		DBInstanceIdentifier: aws.String(cluster),
	})
	if err != nil {
		exitErrorf("Error occurred while waiting for snapshot to be created in cluster, %v", cluster)
	}

	fmt.Printf("Snapshot %q successfully created in cluster\n", cluster)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[rds.go.create_cluster_snapshot]
