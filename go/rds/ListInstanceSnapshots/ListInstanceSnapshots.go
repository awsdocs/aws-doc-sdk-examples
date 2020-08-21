// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.list_instance_snapshots]
package main

// snippet-start:[rds.go.list_instance_snapshots.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
)
// snippet-end:[rds.go.list_instance_snapshots.imports]

// GetInstanceSnapShots retrieves your Amazon RDS instance snapshots
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of snapshopts and nil
//     Otherwise, nil and an error from the call to DescribeDBSnapshots
func GetInstanceSnapShots(sess *session.Session) (*rds.DescribeDBSnapshotsOutput, error) {
    svc := rds.New(sess)

    result, err := svc.DescribeDBSnapshots(nil)
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[rds.go.list_instance_snapshots.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[rds.go.list_instance_snapshots.session]

    result, err := GetInstanceSnapShots(sess)
    if err != nil {
        fmt.Println("Got an error retrieving instance snapshots:")
        fmt.Println(err)
        return
    }

    if len(result.DBSnapshots) < 1 {
        fmt.Println("Could not find any instance snapshots")
        return
    }

    for _, s := range result.DBSnapshots {
        fmt.Printf("* " + *s.DBSnapshotIdentifier + " with status " + *s.Status)
    }
}
// snippet-end:[rds.go.list_instance_snapshots]
