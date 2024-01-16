// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"

	"fmt"
	"os"
)

// Gets the ACL for a bucket object
//
// Usage:
//
//	go run s3_get_bucket_object_acl.go BUCKET OBJECT
func main() {
	if len(os.Args) != 3 {
		exitErrorf("Bucket and object names required\nUsage: go run", os.Args[0], "BUCKET OBJECT")
	}

	bucket := os.Args[1]
	key := os.Args[2]

	// Initialize a session that loads credentials from the shared credentials file ~/.aws/credentials
	// and the region from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create S3 service client
	svc := s3.New(sess)

	// Get bucket ACL
	result, err := svc.GetObjectAcl(&s3.GetObjectAclInput{Bucket: &bucket, Key: &key})
	if err != nil {
		exitErrorf(err.Error())
	}

	fmt.Println("Owner:", *result.Owner.DisplayName)
	fmt.Println("")
	fmt.Println("Grants")

	for _, g := range result.Grants {
		fmt.Println("  Grantee:   ", *g.Grantee.DisplayName)
		fmt.Println("  Type:      ", *g.Grantee.Type)
		fmt.Println("  Permission:", *g.Permission)
		fmt.Println("")
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
