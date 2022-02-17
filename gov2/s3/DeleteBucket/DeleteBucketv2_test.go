package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type S3DeleteBucketImpl struct{}

func (dt S3DeleteBucketImpl) DeleteBucket(ctx context.Context,
	params *s3.DeleteBucketInput,
	optFns ...func(*s3.Options)) (*s3.DeleteBucketOutput, error) {

	output := &s3.DeleteBucketOutput{}

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

func TestDeleteBucket(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := s3.DeleteBucketInput{
		Bucket: &globalConfig.BucketName,
	}

	api := &S3DeleteBucketImpl{}

	_, err = RemoveBucket(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Deleted bucket " + globalConfig.BucketName)
}
