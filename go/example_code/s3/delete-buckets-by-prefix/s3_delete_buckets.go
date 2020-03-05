/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package main

import (
	"flag"
	"fmt"
	"strings"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"
)

func deleteBucketsByPrefix(sess *session.Session, prefix string) error {
	// Create Amazon S3 service client
	svc := s3.New(sess)

	// Empty list to hold names of S3 buckets with prefix
	bucketList := make([]string, 0)

	result, err := svc.ListBuckets(nil)
	if err != nil {
		return err
	}

	for _, b := range result.Buckets {
		// Does bucket name start with prefix
		if strings.HasPrefix(*b.Name, prefix) {
			bucketList = append(bucketList, *b.Name)
		}
	}

	for _, b := range bucketList {
		// First delete any objects in the bucket
		iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
			Bucket: aws.String(b),
		})

		err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
		if err != nil {
			return err
		}

		_, err = svc.DeleteBucket(&s3.DeleteBucketInput{
			Bucket: aws.String(b),
		})
		if err != nil {
			return err
		}

		err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
			Bucket: aws.String(b),
		})
		if err != nil {
			return err
		}
	}

	return nil
}

// Deletes any S3 buckets in the default AWS Region
// that start with the given text
//
// Usage:
//    go run s3_delete_buckets -p PREFIX
func main() {
	prefixPtr := flag.String("p", "", "The prefix of the buckets to delete")
	flag.Parse()
	prefix := *prefixPtr

	if prefix == "" {
		fmt.Println("You must supply a bucket prefix")
		return
	}

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file (~/.aws/credentials)
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	err := deleteBucketsByPrefix(sess, prefix)
	if err != nil {
		fmt.Println("Could not delete buckets with prefix " + prefix)
	} else {
		fmt.Println("Deleted buckets with prefix " + prefix)
	}
}
