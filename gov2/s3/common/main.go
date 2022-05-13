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

func MakeBucket(client s3.Client, name string) {
	//snippet-start:[s3.go-v2.CreateBucket]
	// Create a bucket: We're going to create a bucket to hold content.
	// Best practice is to use the preset private access control list (ACL).
	// If you are not creating a bucket from us-east-1, you must specify a bucket location constraint.
	// Bucket names must conform to several rules; read more at https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html
	_, err := client.CreateBucket(context.TODO(), &s3.CreateBucketInput{
		Bucket:                    aws.String(name),
		ACL:                       types.BucketCannedACLPrivate,
		CreateBucketConfiguration: &types.CreateBucketConfiguration{LocationConstraint: types.BucketLocationConstraintUsWest2},
	})

	if err != nil {
		panic("could not create bucket: " + err.Error())
	}

	//snippet-end:[s3.go-v2.CreateBucket]
}

func AccountBucketOps(client s3.Client, name string) {

	fmt.Println("List buckets: ")
	//snippet-start:[s3.go-v2.ListBuckets]
	listBucketsResult, err := client.ListBuckets(context.TODO(), &s3.ListBucketsInput{})

	if err != nil {
		panic("Couldn't list buckets")
	}

	for _, bucket := range listBucketsResult.Buckets {
		fmt.Printf("Bucket name: %s\t\tcreated at: %v\n", *bucket.Name, bucket.CreationDate)
	}
	//snippet-end:[s3.go-v2.ListBuckets]

	//snippet-start:[s3.go-v2.ListObjects]
	// List objects in the bucket.
	// n.b. object keys in Amazon S3 do not begin with '/'. You do not need to lead your
	// prefix with it.
	fmt.Println("Listing the objects in the bucket:")
	listObjsResponse, err := client.ListObjectsV2(context.TODO(), &s3.ListObjectsV2Input{
		Bucket: aws.String(name),
		Prefix: aws.String(""),
	})

	if err != nil {
		panic("Couldn't list bucket contents")
	}

	for _, object := range listObjsResponse.Contents {
		fmt.Printf("%s (%d bytes, class %v) \n", *object.Key, object.Size, object.StorageClass)
	}
	//snippet-end:[s3.go-v2.ListObjects]
}

func BucketOps(client s3.Client, name string) {

	//snippet-start:[s3.go-v2.PutObject]
	// Place an object in a bucket.
	fmt.Println("Upload an object to the bucket")
	// Get the object body to upload.
	// Image credit: https://unsplash.com/photos/iz58d89q3ss
	stat, err := os.Stat("image.jpg")
	if err != nil {
		panic("Couldn't stat image: " + err.Error())
	}
	file, err := os.Open("image.jpg")

	if err != nil {
		panic("Couldn't open local file")
	}

	_, err = client.PutObject(context.TODO(), &s3.PutObjectInput{
		Bucket:        aws.String(name),
		Key:           aws.String("path/myfile.jpg"),
		Body:          file,
		ContentLength: stat.Size(),
	})

	file.Close()

	if err != nil {
		panic("Couldn't upload file: " + err.Error())
	}

	//snippet-end:[s3.go-v2.PutObject]

	//snippet-start:[s3.go-v2.generate_presigned_url]
	// Get a presigned URL for the object.
	// In order to get a presigned URL for an object, you must
	// create a Presignclient
	fmt.Println("Create Presign client")
	presignClient := s3.NewPresignClient(&client)

	presignResult, err := presignClient.PresignGetObject(context.TODO(), &s3.GetObjectInput{
		Bucket: aws.String(name),
		Key:    aws.String("path/myfile.jpg"),
	})

	if err != nil {
		panic("Couldn't get presigned URL for GetObject")
	}

	fmt.Printf("Presigned URL For object: %s\n", presignResult.URL)

	//snippet-end:[s3.go-v2.generate_presigned_url]
	// Download the file.

	//snippet-start:[s3.go-v2.GetObject]
	fmt.Println("Download a file")
	getObjectResponse, err := client.GetObject(context.TODO(), &s3.GetObjectInput{
		Bucket: aws.String(name),
		Key:    aws.String("path/myfile.jpg"),
	})

	if err == nil {
		file, err = os.Create("download.jpg")

		if err != nil {
			panic("didnt open file to write: " + err.Error())
		}
		written, err := io.Copy(file, getObjectResponse.Body)
		if err != nil {
			panic("Failed to write file contents! " + err.Error())
		} else if written != getObjectResponse.ContentLength {
			panic("wrote a different size than was given to us")
		}
		fmt.Println("Done pulling file")
		file.Close()

	} else {
		panic("Couldn't download object")
	}
	//snippet-end:[s3.go-v2.GetObject]

	//snippet-start:[s3.go-v2.CopyObject]
	// Copy an object to another name.

	// CopyObject is "Pull an object from the source bucket + path".
	// The semantics of CopySource varies depending on whether you're using Amazon S3 on Outposts,
	// or through access points.
	// See https://docs.aws.amazon.com/AmazonS3/latest/API/API_CopyObject.html#API_CopyObject_RequestSyntax
	fmt.Println("Copy an object from another bucket to our bucket.")
	_, err = client.CopyObject(context.TODO(), &s3.CopyObjectInput{
		Bucket:     aws.String(name),
		CopySource: aws.String(name + "/path/myfile.jpg"),
		Key:        aws.String("other/file.jpg"),
	})

	if err != nil {
		panic("Couldn't copy the object to a new key")
	}
	//snippet-end:[s3.go-v2.CopyObject]
}

func BucketDelOps(client s3.Client, name string) {

	//snippet-start:[s3.go-v2.DeleteObject]
	// Delete a single object.
	fmt.Println("Delete an object from a bucket")
	_, err := client.DeleteObject(context.TODO(), &s3.DeleteObjectInput{
		Bucket: aws.String(name),
		Key:    aws.String("other/file.jpg"),
	})
	if err != nil {
		panic("Couldn't delete object!")
	}

	//snippet-end:[s3.go-v2.DeleteObject]

	//snippet-start:[s3.go-v2.EmptyBucket]
	// Delete all objects in a bucket.

	fmt.Println("Delete the objects in a bucket")
	// Note: For versioned buckets, you must also delete all versions of
	// all objects within the bucket with ListVersions and DeleteVersion.
	listObjectsV2Response, err := client.ListObjectsV2(context.TODO(),
		&s3.ListObjectsV2Input{
			Bucket: aws.String(name),
		})

	for {

		if err != nil {
			panic("Couldn't list objects...")
		}
		for _, item := range listObjectsV2Response.Contents {
			fmt.Printf("- Deleting object %s\n", *item.Key)
			_, err = client.DeleteObject(context.Background(), &s3.DeleteObjectInput{
				Bucket: aws.String(name),
				Key:    item.Key,
			})

			if err != nil {
				panic("Couldn't delete items")
			}
		}

		if listObjectsV2Response.IsTruncated {
			listObjectsV2Response, err = client.ListObjectsV2(context.TODO(),
				&s3.ListObjectsV2Input{
					Bucket:            aws.String(name),
					ContinuationToken: listObjectsV2Response.ContinuationToken,
				})
		} else {
			break
		}

	}
	//snippet-end:[s3.go-v2.EmptyBucket]

	// snippet-start:[s3.go-v2.DeleteBucket]
	fmt.Println("Delete a bucket")
	// Delete the bucket.

	_, err = client.DeleteBucket(context.TODO(), &s3.DeleteBucketInput{
		Bucket: aws.String(name),
	})
	if err != nil {
		panic("Couldn't delete bucket: " + err.Error())
	}
	// snippet-end:[s3.go-v2.DeleteBucket]
}

func main() {
	//snippet-start:[s3.go-v2.s3_basics]

	// This bucket name is 100% unique.
	// Remember that bucket names must be globally unique among all buckets.

	myBucketName := "mybucket-" + (xid.New().String())
	fmt.Printf("Bucket name: %v\n", myBucketName)

	cfg, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		panic("Failed to load configuration")
	}

	s3client := s3.NewFromConfig(cfg)

	MakeBucket(*s3client, myBucketName)
	BucketOps(*s3client, myBucketName)
	AccountBucketOps(*s3client, myBucketName)
	BucketDelOps(*s3client, myBucketName)

	//snippet-end:[s3.go-v2.s3_basics]
}
