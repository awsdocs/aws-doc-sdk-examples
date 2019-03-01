// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[AWS]
// snippet-sourcedescription:[rds_create_cluster_snapshot creates a snapshot of an RDS cluster.]
// snippet-keyword:[Amazon Relational Database Service]
// snippet-keyword:[Amazon RDS]
// snippet-keyword:[CreateDBClusterSnapshot function]
// snippet-keyword:[Go]
// snippet-service:[rds]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
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
//    go run rds_create_cluster_snapshot CLUSTER_NAME
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
