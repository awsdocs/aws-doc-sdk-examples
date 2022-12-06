// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service action stubs that are used by the scenario unit tests.
//
// Each stub expects specific data as input and returns specific data as an output.
// If an error is specified, it is raised by the stubber.
package stubs

import (
	"fmt"
	"io"

	"github.com/aws/aws-sdk-go-v2/aws"
	v4 "github.com/aws/aws-sdk-go-v2/aws/signer/v4"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListBuckets(buckets []types.Bucket, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ListBuckets",
		Input:         &s3.ListBucketsInput{},
		Output: &s3.ListBucketsOutput{
			Buckets: buckets,
		},
		Error: raiseErr,
	}
}

func StubHeadBucket(bucketName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "HeadBucket",
		Input: &s3.HeadBucketInput{
			Bucket: aws.String(bucketName),
		},
		Output: &s3.HeadBucketOutput{},
		Error:  raiseErr,
	}
}

func StubCreateBucket(bucketName string, region string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateBucket",
		Input: &s3.CreateBucketInput{
			Bucket: aws.String(bucketName),
			CreateBucketConfiguration: &types.CreateBucketConfiguration{
				LocationConstraint: types.BucketLocationConstraint(region),
			},
		},
		Output: &s3.CreateBucketOutput{},
		Error:  raiseErr,
	}
}

func StubPutObject(bucketName string, objectKey string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "PutObject",
		Input: &s3.PutObjectInput{
			Bucket: aws.String(bucketName),
			Key:    aws.String(objectKey),
		},
		Output:       &s3.PutObjectOutput{},
		Error:        raiseErr,
		IgnoreFields: []string{"Body"},
	}
}

func StubCreateMultipartUpload(bucketName string, objectKey string, uploadId string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreateMultipartUpload",
		Input: &s3.CreateMultipartUploadInput{
			Bucket: aws.String(bucketName),
			Key:    aws.String(objectKey),
		},
		Output: &s3.CreateMultipartUploadOutput{
			Bucket:   aws.String(bucketName),
			Key:      aws.String(objectKey),
			UploadId: aws.String(uploadId),
		},
		Error: raiseErr,
	}
}

func StubUploadPart(bucketName string, objectKey string, uploadId string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "UploadPart",
		Input: &s3.UploadPartInput{
			Bucket:   aws.String(bucketName),
			Key:      aws.String(objectKey),
			UploadId: aws.String(uploadId),
		},
		Output:        &s3.UploadPartOutput{},
		SkipErrorTest: true,
		Error:         raiseErr,
		IgnoreFields:  []string{"Body", "PartNumber"},
	}
}

func StubCompleteMultipartUpload(bucketName string, objectKey string, uploadId string,
	partNumbers []int32, raiseErr *testtools.StubError) testtools.Stub {
	var completedParts []types.CompletedPart
	for _, partNumber := range partNumbers {
		completedParts = append(completedParts, types.CompletedPart{PartNumber: partNumber})
	}
	return testtools.Stub{
		OperationName: "CompleteMultipartUpload",
		Input: &s3.CompleteMultipartUploadInput{
			Bucket:          aws.String(bucketName),
			Key:             aws.String(objectKey),
			UploadId:        aws.String(uploadId),
			MultipartUpload: &types.CompletedMultipartUpload{Parts: completedParts},
		},
		Output: &s3.CompleteMultipartUploadOutput{
			Bucket: aws.String(bucketName),
			Key:    aws.String(objectKey),
		},
		Error: raiseErr,
	}
}

func StubGetObject(bucketName string, objectKey string, byteRange *string, body io.ReadCloser, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetObject",
		Input: &s3.GetObjectInput{
			Bucket: aws.String(bucketName),
			Key:    aws.String(objectKey),
			Range:  byteRange,
		},
		Output: &s3.GetObjectOutput{
			Body: body,
		},
		Error: raiseErr,
	}
}

func StubCopyObject(bucketSource string, objectSource string, bucketDest string, objectDest string,
	raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CopyObject",
		Input: &s3.CopyObjectInput{
			Bucket:     aws.String(bucketDest),
			CopySource: aws.String(fmt.Sprintf("%v/%v", bucketSource, objectSource)),
			Key:        aws.String(objectDest),
		},
		Output: &s3.CopyObjectOutput{},
		Error:  raiseErr,
	}
}

func StubListObjectsV2(bucketName string, keys []string, raiseErr *testtools.StubError) testtools.Stub {
	var objects []types.Object
	for _, key := range keys {
		objects = append(objects, types.Object{Key: aws.String(key)})
	}
	return testtools.Stub{
		OperationName: "ListObjectsV2",
		Input: &s3.ListObjectsV2Input{
			Bucket: aws.String(bucketName),
		},
		Output: &s3.ListObjectsV2Output{
			Contents: objects,
		},
		Error: raiseErr,
	}
}

func StubDeleteObjects(bucketName string, keys []string, raiseErr *testtools.StubError) testtools.Stub {
	var objectIds []types.ObjectIdentifier
	for _, key := range keys {
		objectIds = append(objectIds, types.ObjectIdentifier{Key: aws.String(key)})
	}
	return testtools.Stub{
		OperationName: "DeleteObjects",
		Input: &s3.DeleteObjectsInput{
			Bucket: aws.String(bucketName),
			Delete: &types.Delete{Objects: objectIds},
		},
		Output: &s3.DeleteObjectsOutput{},
		Error:  raiseErr,
	}
}

func StubDeleteBucket(bucketName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteBucket",
		Input: &s3.DeleteBucketInput{
			Bucket: aws.String(bucketName),
		},
		Output: &s3.DeleteBucketOutput{},
		Error:  raiseErr,
	}
}

func StubPresignedRequest(method string, bucketName string, objectKey string, raiseErr *testtools.StubError) testtools.Stub {
	var opName string
	var input interface{}
	switch method {
	case "PUT":
		opName = "PutObject"
		input = &s3.PutObjectInput{Bucket: aws.String(bucketName), Key: aws.String(objectKey)}
	case "GET":
		opName = "GetObject"
		input = &s3.GetObjectInput{Bucket: aws.String(bucketName), Key: aws.String(objectKey)}
	case "DELETE":
		opName = "DeleteObject"
		input = &s3.DeleteObjectInput{Bucket: aws.String(bucketName), Key: aws.String(objectKey)}
	}
	return testtools.Stub{
		OperationName: opName,
		Input:         input,
		Output:        &v4.PresignedHTTPRequest{URL: "test-url", Method: "PUT"},
		Error:         raiseErr,
	}
}
