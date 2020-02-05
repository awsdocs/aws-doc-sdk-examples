package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"

	"flag"
	"fmt"
	"os"
	"strings"
)

func deleteBucketsByPrefix(sess *session.Session, prefix string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Get and save the list of S3 buckets
	bucketList := make([]string, 0)

	result, err := svc.ListBuckets(nil)
	if err != nil {
		fmt.Println("Could not list buckets")
		os.Exit(1)
	}

	for _, b := range result.Buckets {
		// Does bucket name start with prefix
		if strings.HasPrefix(*b.Name, prefix) {
			bucketList = append(bucketList, *b.Name)
		}
	}

	for _, bucket := range bucketList {
		iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
			Bucket: aws.String(bucket),
		})

		err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
		if err != nil {
			fmt.Println("Unable to delete objects from bucket ", bucket)
			continue
		}

		_, err = svc.DeleteBucket(&s3.DeleteBucketInput{
			Bucket: aws.String(bucket),
		})
		if err != nil {
			fmt.Println("Unable to delete bucket " + bucket)
		}

		err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
			Bucket: aws.String(bucket),
		})
		if err != nil {
			fmt.Println("Error occurred while waiting for bucket to be deleted")
		}

		fmt.Println("Successfully deleted bucket: " + bucket)
	}
	return nil
}

// Deletes any S3 buckets in the default region
// that start with the given text
//
// Usage:
//    go run s3_delete_buckets BUCKET_PREFIX [-d}
func main() {
	prefixPtr := flag.String("p", "", "The prefix of the buckets to delete")
	flag.Parse()
	prefix := *prefixPtr

	if prefix == "" {
		fmt.Println("You must supply a bucket prefix")
		os.Exit(1)
	}

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file. (~/.aws/credentials).
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
