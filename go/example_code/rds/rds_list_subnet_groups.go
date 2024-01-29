// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[rds.go.describe_db_subnet_groups]
package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rds"
)

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create RDS service client
	svc := rds.New(sess)

	result, err := svc.DescribeDBSubnetGroups(nil)
	if err != nil {
		exitErrorf("Unable to list subnets groups, %v", err)
	}

	for _, s := range result.DBSubnetGroups {
		fmt.Printf("* %s in VpcId: %s\n",
			aws.StringValue(s.DBSubnetGroupName), aws.StringValue(s.VpcId))
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// snippet-end:[rds.go.describe_db_subnet_groups]
