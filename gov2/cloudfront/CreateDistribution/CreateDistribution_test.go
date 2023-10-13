// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"errors"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudfront"
	"github.com/aws/aws-sdk-go-v2/service/cloudfront/types"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type MockCFDistributionAPI struct {
	s3Client         *s3.Client
	cloudfrontClient *cloudfront.Client
}

func (m *MockCFDistributionAPI) CreateDistribution(bucketName, certificateSSLArn, domain string) (*cloudfront.CreateDistributionOutput, error) {
	if bucketName == "" || certificateSSLArn == "" || domain == "" {
		return nil, errors.New("bucket name, certificate SSL ARN, and domain are required")
	}
	return &cloudfront.CreateDistributionOutput{
		Distribution: &types.Distribution{
			ARN:        aws.String("arn:aws:cloudfront::1234567890:distribution/AAAAAAAAAAAA"),
			DomainName: aws.String(domain),
			DistributionConfig: &types.DistributionConfig{
				ViewerCertificate: &types.ViewerCertificate{
					ACMCertificateArn: aws.String(certificateSSLArn),
				},
			},
		},
	}, nil
}

func (m *MockCFDistributionAPI) createoriginAccessIdentity(domainName string) (string, error) {
	return domainName, nil
}

func createMockCFDistribution(s3client *s3.Client, cloudfront *cloudfront.Client) CFDistributionAPI {
	return &MockCFDistributionAPI{
		s3Client:         s3client,
		cloudfrontClient: cloudfront,
	}
}

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

	mockCFDistribution := createMockCFDistribution(s3Client, cloudfrontClient)

	bucketName := "example-com"
	certificateSSLArn := "arn:aws:acm:ap-northeast-2:123456789000:certificate/000000000-0000-0000-0000-000000000000"
	domain := "example.com"

	result, err := mockCFDistribution.CreateDistribution(bucketName, certificateSSLArn, domain)

	if err != nil {
		t.Error(err)
		return
	}

	t.Log("Created Distribution with ARN: " + *result.Distribution.ARN + " for name: " + *result.Distribution.DomainName)
}
