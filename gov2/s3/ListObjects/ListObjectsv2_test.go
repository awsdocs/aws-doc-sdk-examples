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
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
)

type S3ListObjectsImpl struct{}

func (dt S3ListObjectsImpl) ListObjectsV2(ctx context.Context,
	params *s3.ListObjectsV2Input,
	optFns ...func(*s3.Options)) (*s3.ListObjectsV2Output, error) {

	// Create a dummy list of two objects
	objects := []types.Object{
		{Key: aws.String("item1")},
		{Key: aws.String("item2")},
	}

	output := &s3.ListObjectsV2Output{
		Contents: objects,
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
		msg := "You must supply a value for BucketName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestListObjects(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := s3.ListObjectsV2Input{
		Bucket: &globalConfig.BucketName,
	}

	api := &S3ListObjectsImpl{}

	resp, err := GetObjects(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("#items in "+globalConfig.BucketName+":", len(resp.Contents))

	for _, i := range resp.Contents {
		t.Log("  " + *i.Key)
	}
}
