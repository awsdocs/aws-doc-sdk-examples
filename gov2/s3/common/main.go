// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// Go V2 SDK examples: Common S3 actions

package main

import (
	"context"
	"fmt"
	"io"
	"os"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/rs/xid"
)

func main() {
	//snippet-start:[s3.go-v2.s3_basics]

	// This bucket name is 100% unique.
	// Remember that bucket names must be globally unique among all buckets.

	myBucketName := "myBucket-" + (xid.New().String())
	fmt.Printf("Bucket name: %v\n", myBucketName)

	cfg, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		panic("Failed to load configuration")
	}

	s3client := s3.NewFromConfig(cfg)

	//snippet-start:[s3.go-v2.ListBuckets]
	listBucketsResult, err := s3client.ListBuckets(context.TODO(), &s3.ListBucketsInput{})

	if err != nil {
		panic("Couldn't list buckets")
	}

	for _, bucket := range listBucketsResult.Buckets {
		fmt.Printf("Bucket name: %v\t\tcreated at: %v\n", bucket.Name, bucket.CreationDate)
	}
	//snippet-end:[s3.go-v2.ListBuckets]

	//snippet-start:[s3.go-v2.CreateBucket]
	// Create a bucket: We're going to create a bucket to hold content.
	// Best practice is to use the Pre-canned Private ACL.
	_, err = s3client.CreateBucket(context.TODO(), &s3.CreateBucketInput{
		Bucket: aws.String(myBucketName),
		ACL:    types.BucketCannedACLPrivate,
	})

	if err != nil {
		panic("could not create bucket")
	}
	//snippet-end:[s3.go-v2.CreateBucket]

	//snippet-start:[s3.go-v2.PutObject]
	// Place an object in a bucket
	// Get our object body.
	// The included image is https://unsplash.com/photos/iz58d89q3ss
	file, err := os.Open("image.jpg")

	if err != nil {
		panic("Couldn't open local file")
	}
	_, err = s3client.PutObject(context.TODO(), &s3.PutObjectInput{
		Bucket: aws.String(myBucketName),
		Key:    aws.String("path/myfile.jpg"),
		Body:   file,
	})

	file.Close()

	if err != nil {
		panic("Couldn't upload file")
	}

	//snippet-end:[s3.go-v2.PutObject]

	// Get a presigned URL for the object.

	//snippet-start:[s3.go-v2.generate_presigned_url]
	presignClient := s3.NewPresignClient(s3client)

	presignResult, err := presignClient.PresignGetObject(context.TODO(), &s3.GetObjectInput{
		Bucket: aws.String(myBucketName),
		Key:    aws.String("path/myfile.jpg"),
	})

	if err != nil {
		panic("Couldn't get presigned URL for GetObject")
	}

	fmt.Printf("Presigned URL For object: %v", presignResult.URL)

	//snippet-end:[s3.go-v2.generate_presigned_url]
	// Download the file

	//snippet-start:[s3.go-v2.GetObject]
	getObjectResponse, err := s3client.GetObject(context.TODO(), &s3.GetObjectInput{
		Bucket: aws.String(myBucketName),
		Key:    aws.String("path/myfile.jpg"),
	})

	if err == nil {
		file, _ = os.Open("download.jpg")
		io.Copy(file, getObjectResponse.Body)
		file.Close()
	} else {
		panic("Couldn't download object")
	}
	//snippet-end:[s3.go-v2.GetObject]

	//snippet-start:[s3.go-v2.ListObjects]
	// List objects in the bucket

	listObjsResponse, err := s3client.ListObjectsV2(context.TODO(), &s3.ListObjectsV2Input{
		Bucket: aws.String(myBucketName),
		Prefix: aws.String("/"),
	})

	if err != nil {
		panic("Couldn't list bucket contents")
	}

	for _, object := range listObjsResponse.Contents {
		fmt.Printf("%v (%v bytes, class %v) \n", object.Key, object.Size, object.StorageClass)
	}

	//snippet-end:[s3.go-v2.ListObjects]
	//snippet-start:[s3.go-v2.CopyObject]
	// Copy an object to another name

	// CopyObject is "Pull an object from another place"
	// The semantics of CopySource varies depending on if you are using S3 on Outposts
	// or via Access Points.
	// See https://docs.aws.amazon.com/AmazonS3/latest/API/API_CopyObject.html#API_CopyObject_RequestSyntax
	_, err = s3client.CopyObject(context.TODO(), &s3.CopyObjectInput{
		Bucket:     aws.String(myBucketName),
		CopySource: aws.String(myBucketName + "/path/myfile.jpg"),
		Key:        aws.String("other/file.jpg"),
	})

	if err != nil {
		panic("Couldn't copy the object to a new key")
	}
	//snippet-end:[s3.go-v2.CopyObject]

	//snippet-start:[s3.go-v2.DeleteObject]
	_, err = s3client.DeleteObject(context.TODO(), &s3.DeleteObjectInput{
		Bucket: aws.String(myBucketName),
		Key:    aws.String("other/file.jpg"),
	})
	if err != nil {
		panic("Couldn't delete object!")
	}

	//snippet-end:[s3.go-v2.DeleteObject]

	//snippet-start:[s3.go-v2.EmptyBucket]
	// Delete all objects in a bucket
	// Note: For versioned buckets, you must also delete all versions of
	// all objects within the bucket with ListVersions and DeleteVersion
	listObjectsV2Response, err := s3client.ListObjectsV2(context.TODO(),
		&s3.ListObjectsV2Input{
			Bucket: aws.String(myBucketName),
		})

	for {

		if err != nil {
			panic("Couldn't list objects...")
		}
		ids := make([]types.ObjectIdentifier, listObjectsV2Response.KeyCount)
		for idx, item := range listObjectsV2Response.Contents {
			ids[idx] = types.ObjectIdentifier{Key: item.Key}
		}

		_, err = s3client.DeleteObjects(context.TODO(), &s3.DeleteObjectsInput{
			Bucket: aws.String(myBucketName),
			Delete: &types.Delete{Objects: ids},
		})
		if err != nil {
			panic("Couldn't delete items")
		}

		if listObjectsV2Response.IsTruncated {
			listObjectsV2Response, err = s3client.ListObjectsV2(context.TODO(),
				&s3.ListObjectsV2Input{
					Bucket:            aws.String(myBucketName),
					ContinuationToken: listObjectsV2Response.ContinuationToken,
				})
		} else {
			break
		}

	}
	//snippet-end:[s3.go-v2.EmptyBucket]

	// snippet-start:[s3.go-v2.DeleteBucket]

	// Delete the bucket
	s3client.DeleteBucket(context.TODO(), &s3.DeleteBucketInput{
		Bucket: aws.String(myBucketName),
	})
	// snippet-end:[s3.go-v2.DeleteBucket]

	//snippet-end:[s3.go-v2.s3_basics]
}
