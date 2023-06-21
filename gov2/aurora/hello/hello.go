// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.aurora.Hello]

package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/rds"
)

// main uses the AWS SDK for Go V2 to create an Amazon Aurora client and list up to 20
// DB clusters in your account.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}
	auroraClient := rds.NewFromConfig(sdkConfig)
	const maxClusters = 20
	fmt.Printf("Let's list up to %v DB clusters.\n", maxClusters)
	output, err := auroraClient.DescribeDBClusters(context.TODO(),
		&rds.DescribeDBClustersInput{MaxRecords: aws.Int32(maxClusters)})
	if err != nil {
		fmt.Printf("Couldn't list DB clusters: %v\n", err)
		return
	}
	if len(output.DBClusters) == 0 {
		fmt.Println("No DB clusters found.")
	} else {
		for _, cluster := range output.DBClusters {
			fmt.Printf("DB cluster %v has database %v.\n", *cluster.DBClusterIdentifier,
				*cluster.DatabaseName)
		}
	}
}

// snippet-end:[gov2.aurora.Hello]
