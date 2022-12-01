// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"flag"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/rekognition"
	"github.com/aws/aws-sdk-go-v2/service/rekognition/types"
)

// main will call the Rekognition DetectCustomLabels API with the arguments
// specified from command line flags
// It will print the custom labels detected
func main() {

	bucketName := flag.String("b", "", "The name of the bucket to get the object")
	image := flag.String("i", "", "The path to the image file (JPEG, JPG, PNG)")
	modelArn := flag.String("arn", "", "The rekognition custom labels model arn")
	minConfidence := float32(*flag.Float64("min", 95, "The minimum confidence value "))

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	// Using the Config value, create the DynamoDB client
	svc := rekognition.NewFromConfig(cfg)
	var s3Obj = types.S3Object{
		Bucket: bucketName,
		Name:   image}

	resp, err := svc.DetectCustomLabels(context.TODO(), &rekognition.DetectCustomLabelsInput{
		Image: &types.Image{
			S3Object: &s3Obj},
		ProjectVersionArn: modelArn,
		MinConfidence:     aws.Float32(minConfidence),
	})

	if err != nil {
		log.Fatalf("unable to detect custom labels, %v", err)
	}

	log.Printf("Detected %v labels", len(resp.CustomLabels))

	for _, label := range resp.CustomLabels {
		log.Printf("%v: %v\b", label.Name, label.Confidence)
	}

}
