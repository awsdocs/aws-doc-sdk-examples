// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.GetBucketAcl]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3GetBucketAclAPI defines the interface for the GetBucketAcl function.
// We use this interface to test the function using a mocked service.
type S3GetBucketAclAPI interface {
	GetBucketAcl(ctx context.Context,
		params *s3.GetBucketAclInput,
		optFns ...func(*s3.Options)) (*s3.GetBucketAclOutput, error)
}

// FindBucketAcl retrieves the access control list (ACL) for an Amazon S3 bucket.
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetBucketAclOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to GetBucketAcl
func FindBucketAcl(c context.Context, api S3GetBucketAclAPI, input *s3.GetBucketAclInput) (*s3.GetBucketAclOutput, error) {
	result, err := api.GetBucketAcl(c, input)

	return result, err
}

func main() {
	bucket := flag.String("b", "", "The bucket for which the ACL is returned")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET)")
		return
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	input := &s3.GetBucketAclInput{
		Bucket: bucket,
	}

	result, err := FindBucketAcl(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error retrieving ACL for " + *bucket)
	}

	fmt.Println("Owner:", *result.Owner.DisplayName)
	fmt.Println("")
	fmt.Println("Grants")

	for _, g := range result.Grants {
		// If we add a canned ACL, the name is nil
		if g.Grantee.DisplayName == nil {
			fmt.Println("  Grantee:    EVERYONE")
		} else {
			fmt.Println("  Grantee:   ", *g.Grantee.DisplayName)
		}

		fmt.Println("  Type:      ", string(g.Grantee.Type))
		fmt.Println("  Permission:", string(g.Permission))
		fmt.Println("")
	}
}

// snippet-end:[s3.go-v2.GetBucketAcl]
