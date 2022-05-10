package main

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

func TestCreateImage(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := &ec2.CreateImageInput{
		Description: &globalConfig.Description,
		InstanceId:  &globalConfig.InstanceID,
		Name:        &globalConfig.ImageName,
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

	api := &EC2MockImpl{}

	resp, err := MakeImage(context.Background(), api, input)
	if err != nil {
		fmt.Println("Got an error creating image:")
		fmt.Println(err)
		return
	}

	t.Log("ID: " + *resp.ImageId)
}
