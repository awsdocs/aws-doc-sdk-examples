// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.create_instance_snapshot]
package main

// snippet-start:[rds.go.create_instance_snapshot.imports]
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
// snippet-end:[rds.go.create_instance_snapshot.imports]

// MakeInstanceSnapshop creates a snapshot for an Amazon RDS instance
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     instance is the name of the instance
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateDBSnapshot
func MakeInstanceSnapshop(svc rdsiface.RDSAPI, instance *string) error {
    // snippet-start:[rds.go.create_instance_snapshot.call]
    // Get the current date and time to uniquely identify snapshot
    currentTime := time.Now()
    t := currentTime.Format("2006-01-02 15:04:05")
    // Replace space with underscore for snapshot ID
    t = strings.Replace(t, " ", "_", -1)

    _, err := svc.CreateDBSnapshot(&rds.CreateDBSnapshotInput{
        DBInstanceIdentifier: instance,
        DBSnapshotIdentifier: aws.String(*instance + t),
    })
    // snippet-end:[rds.go.create_instance_snapshot.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[rds.go.create_instance_snapshot.args]
    instance := flag.String("i", "", "The name of the instance")
    flag.Parse()

    if *instance == "" {
        fmt.Println("You must supply and instance name (-i INSTANCE)")
        return
    }
    // snippet-end:[rds.go.create_instance_snapshot.args]

    // snippet-start:[rds.go.create_instance_snapshot.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := rds.New(sess)
    // snippet-end:[rds.go.create_instance_snapshot.session]

    err := MakeInstanceSnapshop(svc, instance)
    if err != nil {
        fmt.Println("Got an error creating instance snapshot:")
        fmt.Println(err)
        return
    }

    // Wait until snapshot is created before finishing
    fmt.Println("Waiting for snapshot in instance " + *instance + " to be created")

    // snippet-start:[rds.go.create_instance_snapshot.wait]
    err = svc.WaitUntilDBSnapshotAvailable(&rds.DescribeDBSnapshotsInput{
        DBInstanceIdentifier: instance,
    })
    // snippet-end:[rds.go.create_instance_snapshot.wait]
    if err != nil {
        fmt.Println("Got an error waiting for snapshot to be created:")
        fmt.Println(err)
        return
    }

    fmt.Println("Snapshot for instance " + *instance + " successfully created")
}
// snippet-end:[rds.go.create_instance_snapshot]
