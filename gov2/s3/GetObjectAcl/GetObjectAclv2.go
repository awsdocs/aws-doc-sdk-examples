// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.GetObjectAcl]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3GetObjectAclAPI defines the interface for the GetObjectAcl function.
// We use this interface to test the function using a mocked service.
type S3GetObjectAclAPI interface {
	GetObjectAcl(ctx context.Context,
		params *s3.GetObjectAclInput,
		optFns ...func(*s3.Options)) (*s3.GetObjectAclOutput, error)
}

// FindObjectAcl gets the access control list (ACL) for an Amazon Simple Storage Service (Amazon S3) bucket object
// Inputs:
//     c is the context of the method call, which includes the AWS Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetObjectAclOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to GetObjectAcl
func FindObjectAcl(c context.Context, api S3GetObjectAclAPI, input *s3.GetObjectAclInput) (*s3.GetObjectAclOutput, error) {
	return api.GetObjectAcl(c, input)
}

func main() {
	bucket := flag.String("b", "", "The bucket containing the object")
	objectName := flag.String("o", "", "The bucket object to get ACL from")
	flag.Parse()

	if *bucket == "" || *objectName == "" {
		fmt.Println("You must supply a bucket (-b BUCKET) and object (-o OBJECT)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	input := &s3.GetObjectAclInput{
		Bucket: bucket,
		Key:    objectName,
	}

	result, err := FindObjectAcl(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error getting ACL for " + *objectName)
		return
	}

	fmt.Println("Owner:", *result.Owner.DisplayName)
	fmt.Println("")
	fmt.Println("Grants")

	for _, g := range result.Grants {
		fmt.Println("  Grantee:   ", *g.Grantee.DisplayName)
		fmt.Println("  Type:      ", string(g.Grantee.Type))
		fmt.Println("  Permission:", string(g.Permission))
		fmt.Println("")
	}
}

// snippet-end:[s3.go-v2.GetObjectAcl]
