// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[cloudtrail.go.delete_trail.complete]
package main

// snippet-start:[cloudtrail.go.delete_trail.imports]
import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudtrail"

	"flag"
	"fmt"
)

// snippet-end:[cloudtrail.go.delete_trail.imports]

func main() {
	// Trail name required
	// snippet-start:[cloudtrail.go.delete_trail.vars]
	trailNamePtr := flag.String("n", "", "The name of the trail to delete")

	flag.Parse()

	if *trailNamePtr == "" {
		fmt.Println("You must supply a trail name")
		return
	}
	// snippet-end:[cloudtrail.go.delete_trail.vars]

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	// snippet-start:[cloudtrail.go.delete_trail.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[cloudtrail.go.delete_trail.session]

	// snippet-start:[cloudtrail.go.delete_trail.delete]
	svc := cloudtrail.New(sess)

	_, err := svc.DeleteTrail(&cloudtrail.DeleteTrailInput{Name: aws.String(*trailNamePtr)})
	if err != nil {
		fmt.Println("Got error calling CreateTrail:")
		fmt.Println(err.Error())
		return
	}

	fmt.Println("Successfully deleted trail", *trailNamePtr)
	// snippet-end:[cloudtrail.go.delete_trail.delete]
}

// snippet-end:[cloudtrail.go.delete_trail.complete]
