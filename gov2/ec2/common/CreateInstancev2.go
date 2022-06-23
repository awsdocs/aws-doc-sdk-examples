// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[ec2.go-v2.CreateInstance]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

// EC2CreateInstanceAPI defines the interface for the RunInstances and CreateTags functions.
// We use this interface to test the functions using a mocked service.
type EC2CreateInstanceAPI interface {
	RunInstances(ctx context.Context,
		params *ec2.RunInstancesInput,
		optFns ...func(*ec2.Options)) (*ec2.RunInstancesOutput, error)

	CreateTags(ctx context.Context,
		params *ec2.CreateTagsInput,
		optFns ...func(*ec2.Options)) (*ec2.CreateTagsOutput, error)
}

// MakeInstance creates an Amazon Elastic Compute Cloud (Amazon EC2) instance.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a RunInstancesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to RunInstances.
func MakeInstance(c context.Context, api EC2CreateInstanceAPI, input *ec2.RunInstancesInput) (*ec2.RunInstancesOutput, error) {
	return api.RunInstances(c, input)
}

// MakeTags creates tags for an Amazon Elastic Compute Cloud (Amazon EC2) instance.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a CreateTagsOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateTags.
func MakeTags(c context.Context, api EC2CreateInstanceAPI, input *ec2.CreateTagsInput) (*ec2.CreateTagsOutput, error) {
	return api.CreateTags(c, input)
}

func CreateInstanceCmd() {
	name := flag.String("n", "", "The name of the tag to attach to the instance")
	value := flag.String("v", "", "The value of the tag to attach to the instance")
	flag.Parse()

	if *name == "" || *value == "" {
		fmt.Println("You must supply a name and value for the tag (-n NAME -v VALUE)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := ec2.NewFromConfig(cfg)

	// Create separate values if required.
	minMaxCount := int32(1)

	input := &ec2.RunInstancesInput{
		ImageId:      aws.String("ami-e7527ed7"),
		InstanceType: types.InstanceTypeT2Micro,
		MinCount:     &minMaxCount,
		MaxCount:     &minMaxCount,
	}

	result, err := MakeInstance(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error creating an instance:")
		fmt.Println(err)
		return
	}

	tagInput := &ec2.CreateTagsInput{
		Resources: []string{*result.Instances[0].InstanceId},
		Tags: []types.Tag{
			{
				Key:   name,
				Value: value,
			},
		},
	}

	_, err = MakeTags(context.TODO(), client, tagInput)
	if err != nil {
		fmt.Println("Got an error tagging the instance:")
		fmt.Println(err)
		return
	}

	fmt.Println("Created tagged instance with ID " + *result.Instances[0].InstanceId)
}

// snippet-end:[ec2.go-v2.CreateInstance]
