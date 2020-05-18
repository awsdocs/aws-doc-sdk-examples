// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.monitor_instances]
package main

// snippet-start:[ec2.go.monitor_instances.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.monitor_instances.imports]

// EnableMonitoring enables monitoring for an Amazon EC2 instance.
// Inputs:
//     svc is an Amazon EC2 service client
//     instanceID is ID of an instance
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
func EnableMonitoring(svc ec2iface.EC2API, instanceID *string) (*ec2.MonitorInstancesOutput, error) {
    // snippet-start:[ec2.go.monitor_instances.enable]
    input := &ec2.MonitorInstancesInput{
        InstanceIds: []*string{
            instanceID,
        },
        DryRun: aws.Bool(true),
    }
    _, err := svc.MonitorInstances(input)
    awsErr, ok := err.(awserr.Error)

    if ok && awsErr.Code() == "DryRunOperation" {
        input.DryRun = aws.Bool(false)
        result, err := svc.MonitorInstances(input)
        // snippet-end:[ec2.go.monitor_instances.enable]
        if err != nil {
            return nil, err
        }

        return result, nil
    }

    return nil, err
}

// DisableMonitoring disable monitoring for an Amazon EC2 instance.
// Inputs:
//     svc is an Amazon EC2 service client
//     instanceID is ID of an instance
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
func DisableMonitoring(svc ec2iface.EC2API, instanceID *string) (*ec2.UnmonitorInstancesOutput, error) {
    // snippet-start:[ec2.go.monitor_instances.disable]
    input := &ec2.UnmonitorInstancesInput{
        InstanceIds: []*string{
            instanceID,
        },
        DryRun: aws.Bool(true),
    }
    _, err := svc.UnmonitorInstances(input)
    awsErr, ok := err.(awserr.Error)
    if ok && awsErr.Code() == "DryRunOperation" {
        input.DryRun = aws.Bool(false)
        result, err := svc.UnmonitorInstances(input)
        // snippet-end:[ec2.go.monitor_instances.disable]
        if err != nil {
            return nil, err
        }

        return result, nil
    }

    return nil, err
}

func main() {
    // snippet-start:[ec2.go.monitor_instances.args]
    monitor := flag.String("m", "", "ON to enable monitoring; OFF to disable monitoring")
    instanceID := flag.String("i", "", "The ID of the instance to monitor")
    flag.Parse()

    if *instanceID == "" || (*monitor != "ON" && *monitor != "OFF") {
        fmt.Println("You must supply the ID of the instance to enable/disable monitoring (-i INSTANCE-ID)")
        fmt.Println("and whether to enable monitoring (-m ON) of disable monitoring (-m OFF)")
        return
    }
    // snippet-end:[ec2.go.monitor_instances.args]

    // snippet-start:[ec2.go.monitor_instances.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.monitor_instances.session]

    // Turn monitoring on
    if *monitor == "ON" {
        result, err := EnableMonitoring(svc, instanceID)
        if err != nil {
            fmt.Println("Got an error enablying monitoring for instance:")
            fmt.Println(err)
            return
        }

        fmt.Println("Success", result.InstanceMonitorings)
    } else if *monitor == "OFF" {
        result, err := DisableMonitoring(svc, instanceID)
        if err != nil {
            fmt.Println("Got an error disablying monitoring for instance:")
            fmt.Println(err)
            return
        }

        fmt.Println("Success", result.InstanceMonitorings)
    }
}
// snippet-end:[ec2.go.monitor_instances]
