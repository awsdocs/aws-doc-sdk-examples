// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.list_instances]
package main

// snippet-start:[rds.go.list_instances.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
)
// snippet-end:[rds.go.list_instances.imports]

// GetInstances retrieves a list of your Amazon RDS instances
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of instances and nil
//     Otherwise, nil and an error from the call to DescribeDBInstances
func GetInstances(sess *session.Session) (*rds.DescribeDBInstancesOutput, error) {
    // snippet-start:[rds.go.list_instances.call]
    svc := rds.New(sess)

    result, err := svc.DescribeDBInstances(nil)
    // snippet-end:[rds.go.list_instances.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[rds.go.list_instances.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[rds.go.list_instances.session]

    result, err := GetInstances(sess)
    if err != nil {
        fmt.Println("Got an error retrieving instances:")
        fmt.Println(err)
        return
    }

    // snippet-start:[rds.go.list_instances.display]
    if len(result.DBInstances) < 1 {
        fmt.Println("Could not find any instances")
        return
    }

    fmt.Println("Instances:")

    for _, d := range result.DBInstances {
        fmt.Println("* " + *d.DBInstanceIdentifier + " created on " + d.InstanceCreateTime.Format("2006-01-02 15:04:05 Monday"))
    }
    // snippet-end:[rds.go.list_instances.display]
}
// snippet-end:[rds.go.list_instances]
