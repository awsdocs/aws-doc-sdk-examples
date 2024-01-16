// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[s3.go.get_bucket_website]
package main

// snippet-start:[s3.go.get_bucket_website.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// snippet-end:[s3.go.get_bucket_website.imports]

// RetrieveWebSite retrieves the bucket's website configuration.
func RetrieveWebSite(sess *session.Session, bucket *string) (*s3.GetBucketWebsiteOutput, error) {
	// snippet-start:[s3.go.get_bucket_website.call]
	svc := s3.New(sess)

	result, err := svc.GetBucketWebsite(&s3.GetBucketWebsiteInput{
		Bucket: bucket,
	})
	// snippet-end:[s3.go.get_bucket_website.call]
	if err != nil {
		return nil, err
	}

	return result, nil
}

func main() {
	// snippet-start:[s3.go.get_bucket_website.args]
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply the name of a bucket (-b BUCKET")
		return
	}
	// snippet-end:[s3.go.get_bucket_website.args]

	// snippet-start:[s3.go.get_bucket_website.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[s3.go.get_bucket_website.session]

	result, err := RetrieveWebSite(sess, bucket)
	// snippet-start:[s3.go.get_bucket_website.error]
	if err != nil {
		awsErr, ok := err.(awserr.Error)
		if ok && awsErr.Code() == "NoSuchWebsiteConfiguration" {
			fmt.Println("Bucket does not have a website configuration")
		} else {
			fmt.Println("Got error retrieving bucket website configuration:")
			fmt.Println(err)
		}

		return
	}
	// snippet-end:[s3.go.get_bucket_website.error]

	// snippet-start:[s3.go.get_bucket_website.print]
	fmt.Println("Website Configuration:")
	fmt.Println(result)
	// snippet-end:[s3.go.get_bucket_website.print]
}

// snippet-end:[s3.go.get_bucket_website]
