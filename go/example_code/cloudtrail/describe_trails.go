// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudtrail"
)

func main() {
	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create CloudTrail client
	svc := cloudtrail.New(sess)

	resp, err := svc.DescribeTrails(&cloudtrail.DescribeTrailsInput{TrailNameList: nil})
	if err != nil {
		fmt.Println("Got error calling CreateTrail:")
		fmt.Println(err.Error())
		return
	}

	fmt.Println("Found", len(resp.TrailList), "trail(s)")
	fmt.Println("")

	for _, trail := range resp.TrailList {
		fmt.Println("Trail name:  " + *trail.Name)
		fmt.Println("Bucket name: " + *trail.S3BucketName)
		fmt.Println("")
	}
}
