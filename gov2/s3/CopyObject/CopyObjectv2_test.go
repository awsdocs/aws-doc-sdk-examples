// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"net/url"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
)

type S3CopyObjectImpl struct{}

func (dt S3CopyObjectImpl) CopyObject(ctx context.Context,
	params *s3.CopyObjectInput,
	optFns ...func(*s3.Options)) (*s3.CopyObjectOutput, error) {
	result := &types.CopyObjectResult{
		LastModified: aws.Time(time.Now()),
	}

	output := &s3.CopyObjectOutput{
		CopyObjectResult: result,
	}

	return output, nil
}

type Config struct {
	SourceBucket      string `json:"SourceBucket"`
	DestinationBucket string `json:"DestinationBucket"`
	ObjectKey         string `json:"ObjectKey"`
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

	if globalConfig.SourceBucket == "" || globalConfig.DestinationBucket == "" || globalConfig.ObjectKey == "" {
		msg := "Failed to find SourceBucket, DestinationBucket, or ItemKey value in " + configFileName
		return errors.New(msg)
	}

	t.Log("Source bucket:      " + globalConfig.SourceBucket)
	t.Log("Destination bucket: " + globalConfig.DestinationBucket)
	t.Log("Object key:         " + globalConfig.ObjectKey)

	return nil
}

func TestCopyObject(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := s3.CopyObjectInput{
		CopySource: aws.String(url.PathEscape(globalConfig.SourceBucket)),
		Bucket:     &globalConfig.DestinationBucket,
		Key:        &globalConfig.ObjectKey,
	}

	api := &S3CopyObjectImpl{}

	_, err = CopyItem(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Copied " + globalConfig.ObjectKey + " from " + globalConfig.SourceBucket + " to " + globalConfig.DestinationBucket)
}
