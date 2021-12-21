package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

func TestCreateInstance(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &EC2MockImpl{}

	// Create separate values if required.
	minMaxCount := int32(1)

	input := &ec2.RunInstancesInput{
		ImageId:      aws.String("ami-e7527ed7"),
		InstanceType: types.InstanceTypeT2Micro,
		MinCount:     &minMaxCount,
		MaxCount:     &minMaxCount,
	}

	result, err := MakeInstance(context.Background(), api, input)
	if err != nil {
		t.Log("Got an error creating an instance:")
		t.Log(err)
		return
	}

	tagInput := &ec2.CreateTagsInput{
		Resources: []string{*result.Instances[0].InstanceId},
		Tags: []types.Tag{
			{
				Key:   &globalConfig.TagName,
				Value: &globalConfig.TagValue,
			},
		},
	}

	_, err = MakeTags(context.Background(), api, tagInput)
	if err != nil {
		t.Log("Got an error tagging the instance:")
		t.Log(err)
		return
	}

	t.Log("Created tagged instance with ID " + *result.Instances[0].InstanceId)
}
