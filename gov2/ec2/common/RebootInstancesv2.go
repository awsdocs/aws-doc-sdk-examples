// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[ec2.go-v2.RebootInstances]
package main

import (
	"context"
	"errors"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/smithy-go"
)

// EC2RebootInstancesAPI defines the interface for the RebootInstances function.
// We use this interface to test the function using a mocked service.
type EC2RebootInstancesAPI interface {
	RebootInstances(ctx context.Context,
		params *ec2.RebootInstancesInput,
		optFns ...func(*ec2.Options)) (*ec2.RebootInstancesOutput, error)
}

// RebootInstance reboots an Amazon Elastic Compute Cloud (Amazon EC2) instance.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a RebootInstancesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to RebootInstances.
func RebootInstance(c context.Context, api EC2RebootInstancesAPI, input *ec2.RebootInstancesInput) (*ec2.RebootInstancesOutput, error) {
	resp, err := api.RebootInstances(c, input)

	var apiErr smithy.APIError
	if errors.As(err, &apiErr) && apiErr.ErrorCode() == "DryRunOperation" {
		fmt.Println("User has permission to enable monitoring.")
		input.DryRun = aws.Bool(false)
		return api.RebootInstances(c, input)
	}

	return resp, err
}

func RebootInstanceCmd() {
	instanceID := flag.String("i", "", "The ID of the instance to reboot")
	flag.Parse()

	if *instanceID == "" {
		fmt.Println("You must supply an instance ID (-i INSTANCE-ID")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := ec2.NewFromConfig(cfg)

	input := &ec2.RebootInstancesInput{
		InstanceIds: []string{
			*instanceID,
		},
		DryRun: aws.Bool(true),
	}

	_, err = RebootInstance(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error rebooting the instance")
		fmt.Println(err)
		return
	}

	fmt.Println("Rebooted instance with ID " + *instanceID)
}

// snippet-end:[ec2.go-v2.RebootInstances]
