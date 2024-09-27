// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.redshift.Hello]

package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/redshift"
)

// main uses the AWS SDK for Go V2 to create a Redshift client
// and list up to 10 clusters in your account.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	ctx := context.Background()
	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}
	redshiftClient := redshift.NewFromConfig(sdkConfig)
	count := 20
	fmt.Printf("Let's list up to %v clusters for your account.\n", count)
	result, err := redshiftClient.DescribeClusters(ctx, &redshift.DescribeClustersInput{
		MaxRecords: aws.Int32(int32(count)),
	})
	if err != nil {
		fmt.Printf("Couldn't list clusters for your account. Here's why: %v\n", err)
		return
	}
	if len(result.Clusters) == 0 {
		fmt.Println("You don't have any clusters!")
		return
	}
	for _, cluster := range result.Clusters {
		fmt.Printf("\t%v : %v\n", *cluster.ClusterIdentifier, *cluster.ClusterStatus)
	}
}

// snippet-end:[gov2.redshift.Hello]
