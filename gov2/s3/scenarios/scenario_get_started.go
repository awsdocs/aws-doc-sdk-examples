// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

// snippet-start:[gov2.s3.Scenario_GetStarted]

import (
	"context"
	"fmt"
	"log"
	"os"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/actions"
)

// RunGetStartedScenario is an interactive example that shows you how to use Amazon
// Simple Storage Service (Amazon S3) to create an S3 bucket and use it to store objects.
//
// 1. Create a bucket.
// 2. Upload a local file to the bucket.
// 3. Download an object to a local file.
// 4. Copy an object to a different folder in the bucket.
// 5. List objects in the bucket.
// 6. Delete all objects in the bucket.
// 7. Delete the bucket.
//
// This example creates an Amazon S3 service client from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// It uses a questioner from the `demotools` package to get input during the example.
// This package can be found in the ..\..\demotools folder of this repo.
func RunGetStartedScenario(ctx context.Context, sdkConfig aws.Config, questioner demotools.IQuestioner) {
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
	log.Println("Welcome to the Amazon S3 getting started demo.")
	log.Println(strings.Repeat("-", 88))

	s3Client := s3.NewFromConfig(sdkConfig)
	bucketBasics := actions.BucketBasics{S3Client: s3Client}

	count := 10
	log.Printf("Let's list up to %v buckets for your account:", count)
	buckets, err := bucketBasics.ListBuckets(ctx)
	if err != nil {
		panic(err)
	}
	if len(buckets) == 0 {
		log.Println("You don't have any buckets!")
	} else {
		if count > len(buckets) {
			count = len(buckets)
		}
		for _, bucket := range buckets[:count] {
			log.Printf("\t%v\n", *bucket.Name)
		}
	}

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

	fmt.Println("Let's upload a file to your bucket.")
	smallFile := questioner.Ask("Enter the path to a file you want to upload:",
		demotools.NotEmpty{})
	const smallKey = "doc-example-key"
	err = bucketBasics.UploadFile(ctx, bucketName, smallKey, smallFile)
	if err != nil {
		panic(err)
	}
	log.Printf("Uploaded %v as %v.\n", smallFile, smallKey)
	log.Println(strings.Repeat("-", 88))

	log.Printf("Let's download %v to a file.", smallKey)
	downloadFileName := questioner.Ask("Enter a name for the downloaded file:", demotools.NotEmpty{})
	err = bucketBasics.DownloadFile(ctx, bucketName, smallKey, downloadFileName)
	if err != nil {
		panic(err)
	}
	log.Printf("File %v downloaded.", downloadFileName)
	log.Println(strings.Repeat("-", 88))

	log.Printf("Let's copy %v to a folder in the same bucket.", smallKey)
	folderName := questioner.Ask("Enter a folder name: ", demotools.NotEmpty{})
	err = bucketBasics.CopyToFolder(ctx, bucketName, smallKey, folderName)
	if err != nil {
		panic(err)
	}
	log.Printf("Copied %v to %v/%v.\n", smallKey, folderName, smallKey)
	log.Println(strings.Repeat("-", 88))

	log.Println("Let's list the objects in your bucket.")
	questioner.Ask("Press Enter when you're ready.")
	objects, err := bucketBasics.ListObjects(ctx, bucketName)
	if err != nil {
		panic(err)
	}
	log.Printf("Found %v objects.\n", len(objects))
	var objKeys []string
	for _, object := range objects {
		objKeys = append(objKeys, *object.Key)
		log.Printf("\t%v\n", *object.Key)
	}
	log.Println(strings.Repeat("-", 88))

	if questioner.AskBool("Do you want to delete your bucket and all of its "+
		"contents? (y/n)", "y") {
		log.Println("Deleting objects.")
		err = bucketBasics.DeleteObjects(ctx, bucketName, objKeys)
		if err != nil {
			panic(err)
		}
		log.Println("Deleting bucket.")
		err = bucketBasics.DeleteBucket(ctx, bucketName)
		if err != nil {
			panic(err)
		}
		log.Printf("Deleting downloaded file %v.\n", downloadFileName)
		err = os.Remove(downloadFileName)
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

// snippet-end:[gov2.s3.Scenario_GetStarted]
