package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Retrievs the bucket's website configuration.
//
// Usage:
//    go run s3_get_bucekt_website.go BUCKET_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("bucket name required\nUsage: %s bucket_name", os.Args[0])
	}
	bucket := os.Args[1]

	// Inititalize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create S3 service client
	svc := s3.New(sess)

	// Call S3 to retrieve the website configuration for the bucket
	result, err := svc.GetBucketWebsite(&s3.GetBucketWebsiteInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		// Check for the NoSuchWebsiteConfiguration error code telling us
		// that the bucket does not have a website configured.
		if awsErr, ok := err.(awserr.Error); ok && awsErr.Code() == "NoSuchWebsiteConfiguration" {
			exitErrorf("Bucket %s does not have website configuration\n", bucket)
		}
		exitErrorf("Unable to get bucket website config, %v", err)
	}

	// Print out the details about the bucket's website config.
	fmt.Println("Bucket Website Configuration:")
	fmt.Println(result)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
