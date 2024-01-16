// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"crypto/md5"
	"encoding/base64"
	"fmt"
	"net/http"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Downloads an item from an S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//
//	go run s3_download.go
func main() {
	h := md5.New()
	content := strings.NewReader("")
	content.WriteTo(h)

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	resp, _ := svc.PutObjectRequest(&s3.PutObjectInput{
		Bucket: aws.String("testBucket"),
		Key:    aws.String("testKey"),
	})

	md5s := base64.StdEncoding.EncodeToString(h.Sum(nil))
	resp.HTTPRequest.Header.Set("Content-MD5", md5s)

	url, err := resp.Presign(15 * time.Minute)
	if err != nil {
		fmt.Println("error presigning request", err)
		return
	}

	req, err := http.NewRequest("PUT", url, strings.NewReader(""))
	req.Header.Set("Content-MD5", md5s)
	if err != nil {
		fmt.Println("error creating request", url)
		return
	}

	defClient, err := http.DefaultClient.Do(req)
	fmt.Println(defClient, err)
}
