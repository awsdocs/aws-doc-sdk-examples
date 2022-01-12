// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"strconv"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
)

// EC2DescribeVpcEndpointConnectionsAPI defines the interface for the DescribeVpcEndpointConnections function.
// We use this interface to test the function using a mocked service.
type EC2DescribeVpcEndpointConnectionsAPI interface {
	DescribeVpcEndpointConnections(ctx context.Context,
		params *ec2.DescribeVpcEndpointConnectionsInput,
		optFns ...func(*ec2.Options)) (*ec2.DescribeVpcEndpointConnectionsOutput, error)
}

// GetConnectionInfo retrieves information about your VPC endpoint connections.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a DescribeVpcEndpointConnectionsOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to DescribeVpcEndpointConnections.
func GetConnectionInfo(c context.Context,
	api EC2DescribeVpcEndpointConnectionsAPI,
	input *ec2.DescribeVpcEndpointConnectionsInput) (*ec2.DescribeVpcEndpointConnectionsOutput, error) {
	return api.DescribeVpcEndpointConnections(context.Background(), input)
}

func DescribeVpcEndpointCmd() {
	region := flag.String("r", "us-west-2", "The region to get VPC info from.")
	flag.Parse()

	if *region == "" {
		panic("You cannot supply an empty region")
	}

	cfg, err := config.LoadDefaultConfig(context.TODO(), config.WithRegion(*region))
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := ec2.NewFromConfig(cfg)

	input := &ec2.DescribeVpcEndpointConnectionsInput{}

	resp, err := GetConnectionInfo(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error retrieving information about your VPC endpoint:")
		fmt.Println(err)
		return
	}

	cons := len(resp.VpcEndpointConnections)

	if cons == 0 {
		fmt.Println("Could not find any VCP endpoint connections in " + *region)
		return
	}

	fmt.Println("VPC endpoint: Details:")
	respDecrypted, _ := json.MarshalIndent(resp, "", "\t")
	fmt.Println(string(respDecrypted))

	fmt.Println()
	fmt.Println("Found " + strconv.Itoa(cons) + " VCP endpoint connection(s) in " + *region)
}
