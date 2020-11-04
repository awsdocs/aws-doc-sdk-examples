package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type S3CreateBucketImpl struct{}

func (dt S3CreateBucketImpl) CreateBucket(ctx context.Context,
	params *s3.CreateBucketInput,
	optFns ...func(*s3.Options)) (*s3.CreateBucketOutput, error) {

	output := &s3.CreateBucketOutput{
		Location: aws.String("us-west-2"),
	}

	return output, nil
}

type Config struct {
	BucketName string `json:"BucketName"`
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

	if globalConfig.BucketName == "" {
		msg := "You must supply a BucketName value in " + configFileName
		return errors.New(msg)
	}

	t.Log("BucketName: " + globalConfig.BucketName)

	return nil
}

func TestCreateBucket(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := s3.CreateBucketInput{
		Bucket: &globalConfig.BucketName,
	}

	api := &S3CreateBucketImpl{}

	resp, err := MakeBucket(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Created bucket " + globalConfig.BucketName + " in " + *resp.Location)
}
