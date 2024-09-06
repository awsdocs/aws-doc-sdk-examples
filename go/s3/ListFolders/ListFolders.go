// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[s3.go.list_folders]
package main

// snippet-start:[s3.go.list_folders.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// snippet-end:[s3.go.list_folders.imports]

// ListFolders lists the folders (common prefixes) in an S3 bucket.
// Inputs:
//
//	sess is the current session, which provides configuration for the SDK's service clients
//	bucket is the name of the bucket
//
// Output:
//
//	A list of folder names (prefixes) or an error if listing fails
func ListFolders(sess *session.Session, bucket *string) ([]string, error) {
	// snippet-start:[s3.go.list_folders.call]
	svc := s3.New(sess)

	input := &s3.ListObjectsV2Input{
		Bucket:    bucket,
		Delimiter: aws.String("/"), // Only return folder names
	}

	result, err := svc.ListObjectsV2(input)
	if err != nil {
		return nil, err
	}

	var folders []string
	for _, prefix := range result.CommonPrefixes {
		folders = append(folders, *prefix.Prefix)
	}
	// snippet-end:[s3.go.list_folders.call]
	return folders, nil
}

func main() {
	// snippet-start:[s3.go.list_folders.args]
	bucket := flag.String("b", "", "The bucket to list folders from")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET)")
		return
	}
	// snippet-end:[s3.go.list_folders.args]

	// snippet-start:[s3.go.list_folders.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[s3.go.list_folders.session]

	folders, err := ListFolders(sess, bucket)
	if err != nil {
		fmt.Println("Got an error listing folders in bucket " + *bucket)
		return
	}

	fmt.Println("Folders in bucket:", *bucket)
	for _, folder := range folders {
		fmt.Println(folder)
	}
}

// snippet-end:[s3.go.list_folders]
