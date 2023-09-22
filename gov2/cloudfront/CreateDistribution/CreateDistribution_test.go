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
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting integration test at " + nowString)

	sdkConfig, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	s3Client := s3.NewFromConfig(sdkConfig)
	cloudfrontClient := cloudfront.NewFromConfig(sdkConfig)
	bucketName := "<EXAMPLE-BUCKET-NAME>"
	certificateSSLArn := "<AWS CERTIFICATE MANGER ARN>"
	domain := "<YOUR DOMAIN>"
	result, err := CreateDistribution(s3Client, cloudfrontClient, bucketName, certificateSSLArn, domain)

	if err != nil {
		t.Error(err)
		return
	}
	t.Log("Created Distribution with ARN: " + *result.Distribution.ARN + " for name: " + *result.Distribution.DomainName)
}
