// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudfront.go-v2.CreateDistribution]

package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/aws/middleware"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudfront"
	cloudfrontTypes "github.com/aws/aws-sdk-go-v2/service/cloudfront/types"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	awshttp "github.com/aws/smithy-go/transport/http"
)

// main uses the AWS SDK for Go V2 to create an Amazon CloudFront distribution.
// This example uses the default settings specified in your shared credentials
// and config files.
func CreateDistribution(s3Client *s3.Client, cloudfrontClient *cloudfront.Client, bucketName, originAccessIdentityID, certificateSSLArn, domain string) (*cloudfront.CreateDistributionOutput, error) {
	bucket, err := s3Client.HeadBucket(context.TODO(), &s3.HeadBucketInput{
		Bucket: &bucketName,
	})

	if err != nil {
		return nil, err
	}

	bucketResponseMetadata := middleware.GetRawResponse(bucket.ResultMetadata).(*awshttp.Response)

	region := bucketResponseMetadata.Header.Get("x-amz-bucket-region")
	originDomain := bucketName + ".s3." + region + ".amazonaws.com"

	if err != nil {
		return nil, err
	}

	cloudfrontResponse, err := cloudfrontClient.CreateDistribution(context.TODO(), &cloudfront.CreateDistributionInput{
		DistributionConfig: &cloudfrontTypes.DistributionConfig{
			Enabled:           aws.Bool(true),
			CallerReference:   &originDomain,
			Comment:           &originDomain,
			IsIPV6Enabled:     aws.Bool(false),
			PriceClass:        cloudfrontTypes.PriceClassPriceClass100,
			HttpVersion:       cloudfrontTypes.HttpVersionHttp11,
			DefaultRootObject: aws.String("index.html"),
			Aliases: &cloudfrontTypes.Aliases{
				Quantity: aws.Int32(1),
				Items:    []string{domain},
			},
			ViewerCertificate: &cloudfrontTypes.ViewerCertificate{
				ACMCertificateArn: aws.String(certificateSSLArn),
				SSLSupportMethod:  cloudfrontTypes.SSLSupportMethodSniOnly,
			},
			CustomErrorResponses: &cloudfrontTypes.CustomErrorResponses{
				Quantity: aws.Int32(1),
				Items: []cloudfrontTypes.CustomErrorResponse{
					{
						ErrorCode:          aws.Int32(403),
						ResponseCode:       aws.String("200"),
						ErrorCachingMinTTL: aws.Int64(10),
						ResponsePagePath:   aws.String("/index.html"),
					},
				},
			},
			Origins: &cloudfrontTypes.Origins{
				Quantity: aws.Int32(1),
				Items: []cloudfrontTypes.Origin{
					{
						DomainName: aws.String(originDomain),
						Id:         aws.String(originDomain),
						S3OriginConfig: &cloudfrontTypes.S3OriginConfig{
							OriginAccessIdentity: aws.String("origin-access-identity/cloudfront/" + originAccessIdentityID),
						},
					},
				},
			},
			CacheBehaviors: nil,
			DefaultCacheBehavior: &cloudfrontTypes.DefaultCacheBehavior{
				TargetOriginId:       aws.String(originDomain),
				Compress:             aws.Bool(true),
				MinTTL:               aws.Int64(200),
				ViewerProtocolPolicy: cloudfrontTypes.ViewerProtocolPolicyRedirectToHttps,
				ForwardedValues: &cloudfrontTypes.ForwardedValues{
					Cookies: &cloudfrontTypes.CookiePreference{
						Forward: cloudfrontTypes.ItemSelectionNone,
					},
					QueryString: aws.Bool(true),
				},
				AllowedMethods: &cloudfrontTypes.AllowedMethods{
					Quantity: aws.Int32(2),
					Items: []cloudfrontTypes.Method{
						cloudfrontTypes.MethodGet,
						cloudfrontTypes.MethodHead,
					},
				},
			},
		},
	})

	if err != nil {
		return nil, err
	}

	return cloudfrontResponse, nil
}

func main() {
	sdkConfig, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}

	s3Client := s3.NewFromConfig(sdkConfig)
	cloudfrontClient := cloudfront.NewFromConfig(sdkConfig)
	bucketName := "example-bucket-name"
	originAccessIdentityID := "E12345678" // example id
	certificateSSLArn := "arn:aws:us-east-1:1234567890:certificate/7a4c4086-706d-4f6f-a8a2-2c7cebad7264"
	domain := "aws.example.com"
	result, err := CreateDistribution(s3Client, cloudfrontClient, bucketName, originAccessIdentityID, certificateSSLArn, domain)
	if err != nil {
		fmt.Println("Couldn't Create Distribution. Please Check error message and try again.")
		fmt.Println(err)
		return
	}
	fmt.Println(result.Distribution.ARN)
}

// snippet-end:[cloudfront.go-v2.CreateDistribution]
