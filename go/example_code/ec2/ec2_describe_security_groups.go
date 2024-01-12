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

// Describes the security groups by IDs that are passed into the CLI. Takes
// a space separated list of group IDs as input.
//
// Usage:
//
//	go run ec2_describe_security_groups.go groupId1 groupId2 ...
func main() {
	if len(os.Args) < 2 {
		exitErrorf("Security Group ID required\nUsage: %s group_id ...",
			filepath.Base(os.Args[0]))
	}
	groupIds := os.Args[1:]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create an EC2 service client.
	svc := ec2.New(sess)

	// Retrieve the security group descriptions
	result, err := svc.DescribeSecurityGroups(&ec2.DescribeSecurityGroupsInput{
		GroupIds: aws.StringSlice(groupIds),
	})
	if err != nil {
		if aerr, ok := err.(awserr.Error); ok {
			switch aerr.Code() {
			case "InvalidGroupId.Malformed":
				fallthrough
			case "InvalidGroup.NotFound":
				exitErrorf("%s.", aerr.Message())
			}
		}
		exitErrorf("Unable to get descriptions for security groups, %v", err)
	}

	fmt.Println("Security Group:")
	for _, group := range result.SecurityGroups {
		fmt.Println(group)
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
