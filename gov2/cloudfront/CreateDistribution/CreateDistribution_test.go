// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudfront"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

func TestCreateDistribution(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2023-02-05 15:04:05 Sunday")
	t.Log("Starting unit test at " + nowString)

	sdkConfig, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	s3Client := s3.NewFromConfig(sdkConfig)
	cloudfrontClient := cloudfront.NewFromConfig(sdkConfig)
	bucketName := "example-bucket-name"                                                                  // example id
	certificateSSLArn := "arn:aws:us-east-1:1234567890:certificate/7a4c4086-706d-4f6f-a8a2-2c7cebad7264" //example certificateManagerSSLARN
	domain := "aws.example.com"
	result, err := CreateDistribution(s3Client, cloudfrontClient, bucketName, certificateSSLArn, domain)

	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}
	t.Log("Created Distribution with ARN: " + *result.Distribution.ARN + " for name: " + *result.Distribution.DomainName)
}
