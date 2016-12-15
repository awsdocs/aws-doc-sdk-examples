package main

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Requests the ACL of a S3 Bucket and prints it out.
//
// Usage:
//    go run s3_get_bucket_acl.go BUCKET_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("Bucket name required\nUsage: %s bucket_name",
			filepath.Base(os.Args[0]))
	}
	bucket := os.Args[1]

	// Inititalize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create a S3 service client.
	svc := s3.New(sess)

	// Make the API request to S3 using the bucket name to get
	// the bucket's ACL configuration.
	result, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		exitErrorf("Unable to get bucket %q ACL, %v", bucket, err)
	}

	fmt.Printf("Bucket %q owned by %q\n", bucket,
		aws.StringValue(result.Owner.DisplayName))
	fmt.Println("Grants:")
	for _, grant := range result.Grants {
		fmt.Printf("* name: %s, type: %s, permission: %s\n",
			aws.StringValue(grant.Grantee.DisplayName),
			aws.StringValue(grant.Grantee.Type),
			aws.StringValue(grant.Permission),
		)
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
