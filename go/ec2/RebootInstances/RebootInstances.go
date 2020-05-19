// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.reboot_instances]
package main

// snippet-start:[ec2.go.reboot_instances.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.reboot_instances.imports]

// RestartInstance reboots Amazon Elastic Compute Cloud (Amazon EC2) instance.
// Inputs:
//     svc is an Amazon EC2 service client
//     instanceID is the ID of an instance
// Output:
//     If success, nil
//     Otherwise, an error from the call to RebootInstances
func RestartInstance(svc ec2iface.EC2API, instanceID *string) error {
    // snippet-start:[ec2.go.reboot_instances.call]
    // Set DryRun to true to check to see if the instance exists and we have the
    // necessary permissions to monitor the instance.
    input := &ec2.RebootInstancesInput{
        InstanceIds: []*string{
            instanceID,
        },
        DryRun: aws.Bool(true),
    }
    _, err := svc.RebootInstances(input)
    awsErr, ok := err.(awserr.Error)

    // If the error code is `DryRunOperation` it means we have the necessary
    // permissions to Start this instance
    if ok && awsErr.Code() == "DryRunOperation" {
        // Let's now set dry run to be false. This will allow us to reboot the instances
        input.DryRun = aws.Bool(false)
        _, err := svc.RebootInstances(input)
        // snippet-end:[ec2.go.reboot_instances.call]
        if err != nil {
            return err
        }

        return nil
    }

    return err
}

func main() {
    // snippet-start:[ec2.go.reboot_instances.args]
    instanceID := flag.String("i", "", "The ID of the instance to reboot")
    flag.Parse()

    if *instanceID == "" {
        fmt.Println("You must supply the ID of the instance to reboot")
        return
    }
    // snippet-end:[ec2.go.reboot_instances.args]

    // snippet-start:[ec2.go.reboot_instances.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.reboot_instances.session]

    err := RestartInstance(svc, instanceID)
    if err != nil {
        fmt.Println("Got an error rebooting instance:")
        fmt.Println(err)
        return
    }

    fmt.Println("Rebooted instance")
}
// snippet-end:[ec2.go.reboot_instances]
