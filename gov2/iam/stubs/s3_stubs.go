// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the Amazon Simple Storage Service (Amazon S3) actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListBuckets(names []string, raiseErr *testtools.StubError) testtools.Stub {
	var buckets []types.Bucket
	for _, name := range names {
		buckets = append(buckets, types.Bucket{Name: aws.String(name)})
	}
	return testtools.Stub{
		OperationName: "ListBuckets",
		Input:         &s3.ListBucketsInput{},
		Output:        &s3.ListBucketsOutput{Buckets: buckets},
		Error:         raiseErr,
	}
}