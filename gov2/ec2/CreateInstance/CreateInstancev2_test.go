package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

type EC2CreateInstanceImpl struct{}

func (dt EC2CreateInstanceImpl) RunInstances(ctx context.Context,
	params *ec2.RunInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.RunInstancesOutput, error) {

	// Create a dummy instance and populate the InstanceId value
	instances := []types.Instance{
		{InstanceId: aws.String("aws-docs-example-instanceID")},
	}

	output := &ec2.RunInstancesOutput{
		Instances: instances,
	}

	return output, nil
}

func (dt EC2CreateInstanceImpl) CreateTags(ctx context.Context,
	params *ec2.CreateTagsInput,
	optFns ...func(*ec2.Options)) (*ec2.CreateTagsOutput, error) {
	return &ec2.CreateTagsOutput{}, nil
}

type Config struct {
	TagName  string `json:"TagName"`
	TagValue string `json:"TagValue"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.TagName == "" || globalConfig.TagValue == "" {
		msg := "You must specify a value for TagName and TagValue in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestCreateInstance(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &EC2CreateInstanceImpl{}

	input := &ec2.RunInstancesInput{
		ImageId:      aws.String("ami-e7527ed7"),
		InstanceType: types.InstanceTypeT2Micro,
		MinCount:     1,
		MaxCount:     1,
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
