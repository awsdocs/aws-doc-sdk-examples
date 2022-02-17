// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.start_stop_instances]
package main

// snippet-start:[ec2.go.start_stop_instances.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.start_stop_instances.imports]

// StartInstance starts an Amazon EC2 instance.
// Inputs:
//     svc is an Amazon EC2 service client
//     instanceID is the ID of the instance
// Output:
//     If success, nil
//     Otherwise, an error from the call to StartInstances
func StartInstance(svc ec2iface.EC2API, instanceID *string) error {
    // snippet-start:[ec2.go.start_stop_instances.start]
    input := &ec2.StartInstancesInput{
        InstanceIds: []*string{
            instanceID,
        },
        DryRun: aws.Bool(true),
    }
    _, err := svc.StartInstances(input)
    awsErr, ok := err.(awserr.Error)

    if ok && awsErr.Code() == "DryRunOperation" {
        // Set DryRun to be false to enable starting the instances
        input.DryRun = aws.Bool(false)
        _, err = svc.StartInstances(input)
        // snippet-end:[ec2.go.start_stop_instances.start]
        if err != nil {
            return err
        }

        return nil
    }

    return err
}

// StopInstance stops an Amazon EC2 instance.
// Inputs:
//     svc is an Amazon EC2 service client
//     instance ID is the ID of the instance
// Output:
//     If success, nil
//     Otherwise, an error from the call to StopInstances
func StopInstance(svc ec2iface.EC2API, instanceID *string) error {
    // snippet-start:[ec2.go.start_stop_instances.stop]
    input := &ec2.StopInstancesInput{
        InstanceIds: []*string{
            instanceID,
        },
        DryRun: aws.Bool(true),
    }
    _, err := svc.StopInstances(input)
    awsErr, ok := err.(awserr.Error)
    if ok && awsErr.Code() == "DryRunOperation" {
        input.DryRun = aws.Bool(false)
        _, err = svc.StopInstances(input)
        // snippet-end:[ec2.go.start_stop_instances.stop]
        if err != nil {
            return err
        }

        return nil
    }

    return err
}

func main() {
    // snippet-start:[ec2.go.start_stop_instances.args]
    instanceID := flag.String("i", "", "The ID of the instance to start or stop")
    state := flag.String("s", "", "The state to put the instance in: START or STOP")
    flag.Parse()

    if (*state != "START" && *state != "STOP") || *instanceID == "" {
        fmt.Println("You must supply a START or STOP state and an instance ID")
        fmt.Println("(-s START | STOP -i INSTANCE-ID")
        return
    }
    // snippet-end:[ec2.go.start_stop_instances.args]

    // snippet-start:[ec2.go.start_stop_instances.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.start_stop_instances.session]

    if *state == "START" {
        err := StartInstance(svc, instanceID)
        if err != nil {
            fmt.Println("Got an error starting instance")
            fmt.Println(err)
            return
        }

        fmt.Println("Started instance with ID " + *instanceID)
    } else if *state == "STOP" {
        err := StopInstance(svc, instanceID)
        if err != nil {
            fmt.Println("Got an error stopping the instance")
            fmt.Println(err)
            return
        }

        fmt.Println("Stopped instance with ID " + *instanceID)
    }
}
// snippet-end:[ec2.go.start_stop_instances]
