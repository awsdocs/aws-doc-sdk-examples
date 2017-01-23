/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
	"fmt"
	"os"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Requests the ACL of an S3 Bucket and prints it out.
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

	// Create an S3 service client.
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
