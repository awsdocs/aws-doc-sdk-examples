// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.create_db_cluster_snapshot]
package main

// snippet-start:[rds.go.create_db_cluster_snapshot.imports]
import (
    "flag"
    "fmt"
    "strings"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
    "github.com/aws/aws-sdk-go/service/rds/rdsiface"
)
// snippet-end:[rds.go.create_db_cluster_snapshot.imports]

// MakeClusterSnapshot creates a snapshot for an Amazon RDS cluste
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     clusterID is the ID of the cluster
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateDBClusterSnapshot
func MakeClusterSnapshot(svc rdsiface.RDSAPI, clusterID *string) error {
    // snippet-start:[rds.go.create_db_cluster_snapshot.call]
    // Get the current date and time to uniquely identify snapshot
    currentTime := time.Now()
    t := currentTime.Format("2006-01-02 15:04:05")
    // Replace space with underscore
    t = strings.Replace(t, " ", "_", -1)

    _, err := svc.CreateDBClusterSnapshot(&rds.CreateDBClusterSnapshotInput{
        DBClusterIdentifier:         clusterID,
        DBClusterSnapshotIdentifier: aws.String(*clusterID + t),
    })
    // snippet-end:[rds.go.create_db_cluster_snapshot.call]

    return err
}

func main() {
    // snippet-start:[rds.go.create_db_cluster_snapshot.args]
    clusterID := flag.String("c", "", "The cluster ID")
    flag.Parse()

    if *clusterID == "" {
        fmt.Println("You must supply a cluster ID (-c CLUSTER-ID)")
        return
    }
    // snippet-end:[rds.go.create_db_cluster_snapshot.args]

    // snippet-start:[rds.go.create_db_cluster_snapshot.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := rds.New(sess)
    // snippet-end:[rds.go.create_db_cluster_snapshot.session]

    err := MakeClusterSnapshot(svc, clusterID)
    if err != nil {
        fmt.Println("Got an error creating snapshop for cluster " + *clusterID)
        return
    }

    // Wait until snapshot is created before finishing
    fmt.Println("Waiting for snapshot in cluster " + *clusterID + " to be created")

    // snippet-start:[rds.go.create_db_cluster_snapshot.wait]
    err = svc.WaitUntilDBSnapshotAvailable(&rds.DescribeDBSnapshotsInput{
        DBInstanceIdentifier: clusterID,
    })
    // snippet-end:[rds.go.create_db_cluster_snapshot.wait]
    if err != nil {
        fmt.Println("Got an error waiting for snapshop for cluster " + *clusterID)
        return
    }

    fmt.Printf("Snapshot successfully created for cluster with ID " + *clusterID)
}
// snippet-end:[rds.go.create_db_cluster_snapshot]
