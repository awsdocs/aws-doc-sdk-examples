// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudfront.go-v2.CreateDistribution]

package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudfront"
	cloudfrontTypes "github.com/aws/aws-sdk-go-v2/service/cloudfront/types"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// main uses the AWS SDK for Go V2 to create an Amazon CloudFront distribution.
// This example uses the default settings specified in your shared credentials
// and config files.
func CreateDistribution(s3Client *s3.Client, cloudfrontClient *cloudfront.Client, bucketName, certificateSSLArn, domain string) (*cloudfront.CreateDistributionOutput, error) {
	locationOutput, err := s3Client.GetBucketLocation(context.Background(), &s3.GetBucketLocationInput{Bucket: aws.String(bucketName)})

	if err != nil {
		return nil, err
	}
	originDomain := bucketName + ".s3." + string(locationOutput.LocationConstraint) + ".amazonaws.com"

	if err != nil {
		return nil, err
	}

	originAccessIdentityID, err := createoriginAccessIdentity(cloudfrontClient, domain)
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
				ViewerProtocolPolicy: cloudfrontTypes.ViewerProtocolPolicyRedirectToHttps,
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

func createoriginAccessIdentity(cloudfrontClient *cloudfront.Client, domainName string) (string, error) {
	ctx := context.Background()
	oai, err := cloudfrontClient.CreateCloudFrontOriginAccessIdentity(ctx, &cloudfront.CreateCloudFrontOriginAccessIdentityInput{
		CloudFrontOriginAccessIdentityConfig: &cloudfrontTypes.CloudFrontOriginAccessIdentityConfig{
			CallerReference: aws.String(domainName),
			Comment:         aws.String(domainName),
		},
	})
	if err != nil {
		return "", err
	}
	return *oai.CloudFrontOriginAccessIdentity.Id, nil
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
	bucketName := "<EXAMPLE-BUCKET-NAME>"

	// certificateSSLArn is the ARN value of the certificate issued by the aws certificate manager.
	// When testing, please check and copy and paste the ARN of the pre-issued certificate.
	certificateSSLArn := "<AWS CERTIFICATE MANGER ARN>"

	// domain refers to the domain that will be used in conjunction with cloudfront and route53.
	// For testing, please enter a domain that is registered in AWS route53 and will be used in conjunction with cloudfront.
	domain := "<YOUR DOMAIN>"
	result, err := CreateDistribution(s3Client, cloudfrontClient, bucketName, certificateSSLArn, domain)
	if err != nil {
		fmt.Println("Couldn't create distribution. Please check error message and try again.")
		fmt.Println(err)
		return
	}
	fmt.Println(result.Distribution.ARN)
}

// snippet-end:[cloudfront.go-v2.CreateDistribution]
