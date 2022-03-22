// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/rekognition"
	"github.com/aws/aws-sdk-go-v2/service/rekognition/types"
	rTypes "github.com/aws/aws-sdk-go-v2/service/rekognition/types"
	"github.com/aws/aws-sdk-go/aws"
)

type RekognitionDetectLabelsImpl struct{}

func (dt RekognitionDetectLabelsImpl) DetectLabels(ctx context.Context,
	params *rekognition.DetectLabelsInput,
	optFns ...func(*rekognition.Options)) (*rekognition.DetectLabelsOutput, error) {
	labels := make([]types.Label, 2)
	labels[0] = types.Label{Name: aws.String("aws-doc-example-label1"), Confidence: aws.Float32(float32(0.9))}
	labels[1] = types.Label{Name: aws.String("aws-doc-example-label2"), Confidence: aws.Float32(float32(0.4))}

	output := &rekognition.DetectLabelsOutput{
		Labels: labels,
	}

	return output, nil
}

// Config stores the name of the bucket and jpg/png file to upload.
type Config struct {
	Bucket string `json:"BucketName"`
	Key    string `json:"KeyName"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration() error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.Bucket == "" || globalConfig.Key == "" {
		msg := "You must supply a value for BucketName and KeyName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestDetectLabels(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &RekognitionDetectLabelsImpl{}

	input := &rekognition.DetectLabelsInput{
		Image: &rTypes.Image{
			S3Object: &rTypes.S3Object{
				Bucket: &globalConfig.Bucket,
				Name:   &globalConfig.Key,
			},
		},
	}

	// Get the contrived labels
	resp, err := GetLabels(context.TODO(), *api, input)
	if err != nil {
		t.Log("Got an error retrieving image labels:")
		t.Fatal(err)
	}

	t.Log("Info about " + globalConfig.Key + ":")
	t.Log("  #items:     ", len(resp.Labels))

	// Display the two labels
	for _, l := range resp.Labels {
		t.Log(*l.Name, "confidence:", *l.Confidence)
	}
}
