/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "path/filepath"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)

// Releases an Elastic IP address allocation ID. If the address is associated
// with a EC2 instance the association will be removed.
//
// Usage:
//    go run ec2_release_address.go ALLOCATION_ID
func main() {
    if len(os.Args) != 2 {
        exitErrorf("allocation ID required\nUsage: %s allocation_id",
            filepath.Base(os.Args[0]))
    }
    allocationID := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create an EC2 service client.
    svc := ec2.New(sess)

    // Attempt to release the Elastic IP address.
    _, err := svc.ReleaseAddress(&ec2.ReleaseAddressInput{
        AllocationId: aws.String(allocationID),
    })
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok && aerr.Code() == "InvalidAllocationID.NotFound" {
            exitErrorf("Allocation ID %s does not exist", allocaitonID)
        }
        exitErrorf("Unable to release IP address for allocation %s, %v",
            allocationID, err)
    }

    fmt.Printf("Successfully released allocation ID %s\n", allocationID)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
