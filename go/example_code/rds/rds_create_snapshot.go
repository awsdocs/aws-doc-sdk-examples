/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
    "fmt"
    "os"
)

// Creates a RDS snapshot in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//    go run s3_create_snapshot INSTANCE_NAME
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

    // Create the RDS snapshot
    _, err = svc.CreateDBSnapshot(&rds.CreateDBSnapshotInput{
        DBInstanceIdentifier: aws.String(instance),
    })
    if err != nil {
        exitErrorf("Unable to create snapshot in instance %q, %v", cluster, err)
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
