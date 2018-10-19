//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Starts and stops an Amazon EC2 instance.]
//snippet-keyword:[Amazon Elastic Compute Cloud]
//snippet-keyword:[StartInstances function]
//snippet-keyword:[StopInstances function]
//snippet-keyword:[Go]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
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
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)

// Usage:
// go run main.go <state> <instance id>
//   * state can either be START or STOP
func main() {
    // Load session from shared config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new EC2 client
    svc := ec2.New(sess)

    // Turn monitoring on
    if os.Args[1] == "START" {
        // We set DryRun to true to check to see if the instance exists and we have the
        // necessary permissions to monitor the instance.
        input := &ec2.StartInstancesInput{
            InstanceIds: []*string{
                aws.String(os.Args[2]),
            },
            DryRun: aws.Bool(true),
        }
        result, err := svc.StartInstances(input)
        awsErr, ok := err.(awserr.Error)

        // If the error code is `DryRunOperation` it means we have the necessary
        // permissions to Start this instance
        if ok && awsErr.Code() == "DryRunOperation" {
            // Let's now set dry run to be false. This will allow us to start the instances
            input.DryRun = aws.Bool(false)
            result, err = svc.StartInstances(input)
            if err != nil {
                fmt.Println("Error", err)
            } else {
                fmt.Println("Success", result.StartingInstances)
            }
        } else { // This could be due to a lack of permissions
            fmt.Println("Error", err)
        }
    } else if os.Args[1] == "STOP" { // Turn instances off
        input := &ec2.StopInstancesInput{
            InstanceIds: []*string{
                aws.String(os.Args[2]),
            },
            DryRun: aws.Bool(true),
        }
        result, err := svc.StopInstances(input)
        awsErr, ok := err.(awserr.Error)
        if ok && awsErr.Code() == "DryRunOperation" {
            input.DryRun = aws.Bool(false)
            result, err = svc.StopInstances(input)
            if err != nil {
                fmt.Println("Error", err)
            } else {
                fmt.Println("Success", result.StoppingInstances)
            }
        } else {
            fmt.Println("Error", err)
        }
    }
}
