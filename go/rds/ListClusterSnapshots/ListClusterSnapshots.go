// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.list_cluster_snapshots]
package main

// snippet-start:[rds.go.list_cluster_snapshots.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
)
// snippet-end:[rds.go.list_cluster_snapshots.imports]

// GetClusterSnapshots retrieves your Amazon RDS cluster snapshots
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, a list of your cluster snapshots and nil
//     Otherwise, nil and an error from the call to DescribeDBClusterSnapshots
func GetClusterSnapshots(sess *session.Session) (*rds.DescribeDBClusterSnapshotsOutput, error) {
    // snippet-start:[rds.go.list_cluster_snapshots.call]
    svc := rds.New(sess)

    result, err := svc.DescribeDBClusterSnapshots(nil)
    // snippet-end:[rds.go.list_cluster_snapshots.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[rds.go.list_cluster_snapshots.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[rds.go.list_cluster_snapshots.session]

    result, err := GetClusterSnapshots(sess)
    if err != nil {
        fmt.Println("Got an error retrieving cluster snapshots:")
        fmt.Println(err)
        return
    }

    if len(result.DBClusterSnapshots) < 1 {
        fmt.Println("Could not find any cluster snapshots")
        return
    }

    for _, s := range result.DBClusterSnapshots {
        fmt.Println("* " + *s.DBClusterSnapshotIdentifier + " with status " + *s.Status)
    }
}
// snippet-end:[rds.go.list_cluster_snapshots]
