// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"

	"fmt"
	"os"
)

// Allows person with EMAIL address PERMISSION access to BUCKET OBJECT
// If PERMISSION is missing, they get READ access.
//
// Usage:
//
//	go run s3_put_bucket_acl.go BUCKET OBJECT EMAIL [PERMISSION]
func main() {
	if len(os.Args) < 4 {
		exitErrorf("Bucket name, object name, and email address required; permission optional (READ if omitted)\nUsage: go run", os.Args[0], "BUCKET OBJECT EMAIL [PERMISSION]")
	}

	bucket := os.Args[1]
	key := os.Args[2]
	address := os.Args[3]

	permission := "READ"

	if len(os.Args) == 5 {
		permission = os.Args[4]

		if !(permission == "FULL_CONTROL" || permission == "WRITE" || permission == "WRITE_ACP" || permission == "READ" || permission == "READ_ACP") {
			fmt.Println("Illegal permission value. It must be one of:")
			fmt.Println("FULL_CONTROL, WRITE, WRITE_ACP, READ, or READ_ACP")
			os.Exit(1)
		}
	}

	// Initialize a session that loads credentials from the shared credentials file ~/.aws/credentials
	// and the region from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create S3 service client
	svc := s3.New(sess)

	// Get existing ACL
	result, err := svc.GetObjectAcl(&s3.GetObjectAclInput{Bucket: &bucket, Key: &key})
	if err != nil {
		exitErrorf(err.Error())
	}

	owner := *result.Owner.DisplayName
	ownerId := *result.Owner.ID

	// Existing grants
	grants := result.Grants

	// Create new grantee to add to grants
	userType := "AmazonCustomerByEmail"
	var newGrantee = s3.Grantee{EmailAddress: &address, Type: &userType}
	var newGrant = s3.Grant{Grantee: &newGrantee, Permission: &permission}

	// Add them to the grants
	grants = append(grants, &newGrant)

	params := &s3.PutObjectAclInput{
		Bucket: &bucket,
		Key:    &key,
		AccessControlPolicy: &s3.AccessControlPolicy{
			Grants: grants,
			Owner: &s3.Owner{
				DisplayName: &owner,
				ID:          &ownerId,
			},
		},
	}

	// Set bucket ACL
	_, err = svc.PutObjectAcl(params)
	if err != nil {
		exitErrorf(err.Error())
	}

	fmt.Println("Congratulations. You gave user with email address", address, permission, "permission to bucket", bucket, "object", key)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
