// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[ec2.go-v2.CreateImage]
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

// EC2CreateImageAPI defines the interface for the CreateImage function.
// We use this interface to test the function using a mocked service.
type EC2CreateImageAPI interface {
	CreateImage(ctx context.Context,
		params *ec2.CreateImageInput,
		optFns ...func(*ec2.Options)) (*ec2.CreateImageOutput, error)
}

// MakeImage creates an Amazon Elastic Compute Cloud (Amazon EC2) image.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a CreateImageOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateImage.
func MakeImage(c context.Context, api EC2CreateImageAPI, input *ec2.CreateImageInput) (*ec2.CreateImageOutput, error) {
	return api.CreateImage(c, input)
}

func CreateImageCmd() {
	description := flag.String("d", "", "The description of the image")
	instanceID := flag.String("i", "", "The ID of the instance")
	name := flag.String("n", "", "The name of the image")
	flag.Parse()

	if *description == "" || *instanceID == "" || *name == "" {
		fmt.Println("You must supply an image description, instance ID, and image name")
		fmt.Println("(-d IMAGE-DESCRIPTION -i INSTANCE-ID -n IMAGE-NAME")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := ec2.NewFromConfig(cfg)

	input := &ec2.CreateImageInput{
		Description: description,
		InstanceId:  instanceID,
		Name:        name,
		BlockDeviceMappings: []types.BlockDeviceMapping{
			{
				DeviceName: aws.String("/dev/sda1"),
				NoDevice:   aws.String(""),
			},
			{
				DeviceName: aws.String("/dev/sdb"),
				NoDevice:   aws.String(""),
			},
			{
				DeviceName: aws.String("/dev/sdc"),
				NoDevice:   aws.String(""),
			},
		},
	}

	resp, err := MakeImage(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error creating image:")
		fmt.Println(err)
		return
	}

	fmt.Println("ID: ", resp.ImageId)
}

// snippet-end:[ec2.go-v2.CreateImage]
