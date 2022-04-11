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
	"github.com/aws/aws-sdk-go/aws"
)

type RekognitionDetectFacesImpl struct{}

func (dt RekognitionDetectFacesImpl) DetectFaces(ctx context.Context,
	params *rekognition.DetectFacesInput,
	optFns ...func(*rekognition.Options)) (*rekognition.DetectFacesOutput, error) {
	faces := make([]types.FaceDetail, 2)
	age1 := types.AgeRange{Low: aws.Int32(int32(11)), High: aws.Int32((30))}
	age2 := types.AgeRange{Low: aws.Int32(int32(41)), High: aws.Int32((60))}

	faces[0] = types.FaceDetail{AgeRange: &age1}
	faces[1] = types.FaceDetail{AgeRange: &age2}

	output := &rekognition.DetectFacesOutput{
		FaceDetails: faces,
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

func TestDetectFaces(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &RekognitionDetectFacesImpl{}

	input := &rekognition.DetectFacesInput{
		Image: &types.Image{
			S3Object: &types.S3Object{
				Bucket: &globalConfig.Bucket,
				Name:   &globalConfig.Key,
			},
		},
	}

	// Get the contrived faces
	resp, err := GetFaces(context.TODO(), *api, input)
	if err != nil {
		t.Log("Got an error retrieving image labels:")
		t.Fatal(err)
	}

	t.Log("Info about " + globalConfig.Key + ":")
	t.Log("  #items: ", len(resp.FaceDetails))

	// Display info about the two faces
	for _, f := range resp.FaceDetails {
		t.Log(" Age range from", *f.AgeRange.Low, "to", *f.AgeRange.High)
	}
}
