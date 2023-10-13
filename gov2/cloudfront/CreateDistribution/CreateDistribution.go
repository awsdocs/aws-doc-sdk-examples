// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudfront.go-v2.CreateDistribution]

package main

import (
	"context"
	"errors"
	"flag"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudfront"
	cloudfrontTypes "github.com/aws/aws-sdk-go-v2/service/cloudfront/types"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// CFDistributionAPI defines the interface for the CreateDistribution function.
// We use this interface to test the function using a mocked service.
type CFDistributionAPI interface {
	CreateDistribution(bucketName, certificateSSLArn, domain string) (*cloudfront.CreateDistributionOutput, error)
	createoriginAccessIdentity(domainName string) (string, error)
}

type CFDistributionAPIImpl struct {
	s3Client         *s3.Client
	cloudfrontClient *cloudfront.Client
}

func createCFDistribution(s3client *s3.Client, cloudfront *cloudfront.Client) CFDistributionAPI {
	return &CFDistributionAPIImpl{
		s3Client:         s3client,
		cloudfrontClient: cloudfront,
	}
}

func (c *CFDistributionAPIImpl) CreateDistribution(bucketName, certificateSSLArn, domain string) (*cloudfront.CreateDistributionOutput, error) {
	locationOutput, err := c.s3Client.GetBucketLocation(context.Background(), &s3.GetBucketLocationInput{Bucket: aws.String(bucketName)})

	if err != nil {
		return nil, err
	}
	originDomain := bucketName + ".s3." + string(locationOutput.LocationConstraint) + ".amazonaws.com"

	if err != nil {
		return nil, err
	}

	originAccessIdentityID, err := c.createoriginAccessIdentity(domain)
	if err != nil {
		return nil, err
	}

	cloudfrontResponse, err := c.cloudfrontClient.CreateDistribution(context.TODO(), &cloudfront.CreateDistributionInput{
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

func (c *CFDistributionAPIImpl) createoriginAccessIdentity(domainName string) (string, error) {
	ctx := context.Background()
	oai, err := c.cloudfrontClient.CreateCloudFrontOriginAccessIdentity(ctx, &cloudfront.CreateCloudFrontOriginAccessIdentityInput{
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

var (
	// bucketName is the name of the S3 bucket to create a CloudFront distribution for.
	bucketName = ""
	// certificateSSLArn is the ARN value of the certificate issued by the AWS Certificate Manager (ACM).
	// When testing, please check and copy and paste the ARN of the pre-issued certificate.
	// If you don't know how to create a TLS/SSL certificate using ACM, follow the link below.
	// https://docs.aws.amazon.com/acm/latest/userguide/acm-overview.html
	certificateSSLArn = ""
	// domain refers to the domain that will be used in conjunction with CloudFront and Amazon Route 53.
	// For testing, please enter a domain that is registered in Route 53 and will be used in conjunction with CloudFront.
	domain = ""
)

// main uses the AWS SDK for Go V2 to create an Amazon CloudFront distribution.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {

	flag.StringVar(&bucketName, "bucket", "", "<EXAMPLE-BUCKET-NAME>")
	flag.StringVar(&certificateSSLArn, "cert", "", "<AWS CERTIFICATE MANGER ARN>")
	flag.StringVar(&domain, "domain", "", "<YOUR DOMAIN>")
	flag.Parse()
	if bucketName == "" {
		log.Println(errors.New("please setup bucket name"))
		return
	}

	if certificateSSLArn == "" {
		log.Println(errors.New("please setup certificate ARN"))
		return
	}

	if domain == "" {
		log.Println(errors.New("please setup your domain"))
		return
	}

	sdkConfig, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}

	s3Client := s3.NewFromConfig(sdkConfig)
	cloudfrontClient := cloudfront.NewFromConfig(sdkConfig)

	cfDistribution := createCFDistribution(s3Client, cloudfrontClient)

	result, err := cfDistribution.CreateDistribution(bucketName, certificateSSLArn, domain)
	if err != nil {
		fmt.Println("Couldn't create distribution. Please check error message and try again.")
		fmt.Println(err)
		return
	}
	fmt.Println(result.Distribution.ARN)
}

// snippet-end:[cloudfront.go-v2.CreateDistribution]
