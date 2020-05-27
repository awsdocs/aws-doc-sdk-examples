// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.describe_instances]
package main

// snippet-start:[ec2.go.describe_instances.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)
// snippet-end:[ec2.go.describe_instances.imports]

// GetInstances retrieves information about your Amazon EC2 instances.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, a list of Amazon EC2 instances and nil
//     Otherwise, nil and an error from the call to DescibeInstances
func GetInstances(sess *session.Session) (*ec2.DescribeInstancesOutput, error) {
    // snippet-start:[ec2.go.describe_instances.call]
    svc := ec2.New(sess)
    result, err := svc.DescribeInstances(nil)
    // snippet-end:[ec2.go.describe_instances.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[ec2.go.describe_instances.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[ec2.go.describe_instances.session]

    result, err := GetInstances(sess)
    if err != nil {
        fmt.Println("Got an error retrieving information about your Amazon EC2 instances:")
        fmt.Println(err)
        return
    }

    for _, r := range result.Reservations {
        fmt.Println("Reservation ID: " + *r.ReservationId)
        fmt.Println("Instance IDs:")
        for _, i := range r.Instances {
            fmt.Println("   " + *i.InstanceId)
        }

        fmt.Println("")
    }
}
// snippet-end:[ec2.go.describe_instances]
