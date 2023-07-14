// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.rds.Hello]

package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/rds"
)

// main uses the AWS SDK for Go V2 to create an Amazon Relational Database Service (Amazon RDS)
// client and list up to 20 DB instances in your account.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}
	rdsClient := rds.NewFromConfig(sdkConfig)
	const maxInstances = 20
	fmt.Printf("Let's list up to %v DB instances.\n", maxInstances)
	output, err := rdsClient.DescribeDBInstances(context.TODO(),
		&rds.DescribeDBInstancesInput{MaxRecords: aws.Int32(maxInstances)})
	if err != nil {
		fmt.Printf("Couldn't list DB instances: %v\n", err)
		return
	}
	if len(output.DBInstances) == 0 {
		fmt.Println("No DB instances found.")
	} else {
		for _, instance := range output.DBInstances {
			fmt.Printf("DB instance %v has database %v.\n", *instance.DBInstanceIdentifier,
				*instance.DBName)
		}
	}
}

// snippet-end:[gov2.rds.Hello]
