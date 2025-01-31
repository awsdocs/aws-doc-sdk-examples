// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

// snippet-start:[gov2.s3.Scenario_LargeObjects]

import (
	"context"
	"crypto/rand"
	"log"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/actions"
)

// RunLargeObjectScenario is an interactive example that shows you how to use Amazon
// Simple Storage Service (Amazon S3) to upload and download large objects.
//
// 1. Create a bucket.
// 3. Upload a large object to the bucket by using an upload manager.
// 5. Download a large object by using a download manager.
// 8. Delete all objects in the bucket.
// 9. Delete the bucket.
//
// This example creates an Amazon S3 service client from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// It uses a questioner from the `demotools` package to get input during the example.
// This package can be found in the ..\..\demotools folder of this repo.
func RunLargeObjectScenario(ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner) {
	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
			_, isMock := questioner.(*demotools.MockQuestioner)
			if isMock || questioner.AskBool("Do you want to see the full error message (y/n)?", "y") {
				log.Println(r)
			}
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon S3 large object demo.")
	log.Println(strings.Repeat("-", 88))

	s3Client := s3.NewFromConfig(sdkConfig)
	bucketBasics := actions.BucketBasics{S3Client: s3Client}

	bucketName := questioner.Ask("Let's create a bucket. Enter a name for your bucket:",
		demotools.NotEmpty{})
	bucketExists, err := bucketBasics.BucketExists(ctx, bucketName)
	if err != nil {
		panic(err)
	}
	if !bucketExists {
		err = bucketBasics.CreateBucket(ctx, bucketName, sdkConfig.Region)
		if err != nil {
			panic(err)
		} else {
			log.Println("Bucket created.")
		}
	}
	log.Println(strings.Repeat("-", 88))

	mibs := 30
	log.Printf("Let's create a slice of %v MiB of random bytes and upload it to your bucket. ", mibs)
	questioner.Ask("Press Enter when you're ready.")
	largeBytes := make([]byte, 1024*1024*mibs)
	_, _ = rand.Read(largeBytes)
	largeKey := "doc-example-large"
	log.Println("Uploading...")
	err = bucketBasics.UploadLargeObject(ctx, bucketName, largeKey, largeBytes)
	if err != nil {
		panic(err)
	}
	log.Printf("Uploaded %v MiB object as %v", mibs, largeKey)
	log.Println(strings.Repeat("-", 88))

	log.Printf("Let's download the %v MiB object.", mibs)
	questioner.Ask("Press Enter when you're ready.")
	log.Println("Downloading...")
	largeDownload, err := bucketBasics.DownloadLargeObject(ctx, bucketName, largeKey)
	if err != nil {
		panic(err)
	}
	log.Printf("Downloaded %v bytes.", len(largeDownload))
	log.Println(strings.Repeat("-", 88))

	if questioner.AskBool("Do you want to delete your bucket and all of its "+
		"contents? (y/n)", "y") {
		log.Println("Deleting object.")
		err = bucketBasics.DeleteObjects(ctx, bucketName, []string{largeKey})
		if err != nil {
			panic(err)
		}
		log.Println("Deleting bucket.")
		err = bucketBasics.DeleteBucket(ctx, bucketName)
		if err != nil {
			panic(err)
		}
	} else {
		log.Println("Okay. Don't forget to delete objects from your bucket to avoid charges.")
	}
	log.Println(strings.Repeat("-", 88))

	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// snippet-end:[gov2.s3.Scenario_LargeObjects]
