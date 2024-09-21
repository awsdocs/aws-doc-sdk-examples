// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[go-v2.route53.hello]
package main

import (
	"context"
	"errors"
	"flag"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/route53"
	"github.com/aws/aws-sdk-go-v2/service/route53/types"
)

const CloudFrontHostedZoneID = "Z2FDTNDATAQYW2"
const region = "us-east-1"

var (
	// Define the flags
	// hostedZoneID is the ID of the hosted zone.
	hostedZoneID = ""
	// domain is the domain to point to the CloudFront domain.
	domain = ""
	// cloudfrontDomain is the CloudFront domain.
	cloudfrontDomain = ""
)

// main uses the AWS SDK for Go (v2) to operate an Amazon route53 client and
// change resource record sets to point to a domain.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {

	// Get the hosted zone ID, domain, and CloudFront domain from the user.
	flag.StringVar(&hostedZoneID, "bucket", "", "<HOSTED ZONE ID>")
	flag.StringVar(&cloudfrontDomain, "cert", "", "<CLOUDFRONT DOMAIN>")
	flag.StringVar(&domain, "domain", "", "<YOUR DOMAIN>")
	flag.Parse()
	if hostedZoneID == "" {
		log.Println(errors.New("please setup hosted zone ID"))
		return
	}

	if cloudfrontDomain == "" {
		log.Println(errors.New("please setup CloudFront domain"))
		return
	}

	if domain == "" {
		log.Println(errors.New("please setup your domain"))
		return
	}

	ctx := context.TODO()
	sdkConfig, err := config.LoadDefaultConfig(ctx, config.WithRegion(region))
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}
	route53Client := route53.NewFromConfig(sdkConfig)
	output, err := route53Client.ChangeResourceRecordSets(ctx, &route53.ChangeResourceRecordSetsInput{
		HostedZoneId: aws.String(hostedZoneID),
		ChangeBatch: &types.ChangeBatch{
			Changes: []types.Change{
				{
					Action: types.ChangeActionUpsert,
					ResourceRecordSet: &types.ResourceRecordSet{
						Type: types.RRTypeA,
						Name: aws.String(domain),
						AliasTarget: &types.AliasTarget{
							DNSName:              aws.String(cloudfrontDomain),
							HostedZoneId:         aws.String(CloudFrontHostedZoneID),
							EvaluateTargetHealth: false,
						},
					},
				},
			},
		},
	})
	if err != nil {
		fmt.Printf("Couldn't change resource record sets. Here's why: %v\n", err)
		return
	}
	fmt.Println(output)
}

// snippet-end:[go-v2.route53.hello]
