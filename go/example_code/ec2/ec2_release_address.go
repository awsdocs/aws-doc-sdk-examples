// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
//
//	go run ec2_release_address.go ALLOCATION_ID
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
	_, err = svc.ReleaseAddress(&ec2.ReleaseAddressInput{
		AllocationId: aws.String(allocationID),
	})
	if err != nil {
		if aerr, ok := err.(awserr.Error); ok && aerr.Code() == "InvalidAllocationID.NotFound" {
			exitErrorf("Allocation ID %s does not exist", allocationID)
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
