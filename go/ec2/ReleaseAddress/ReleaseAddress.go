// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.release_address]
package main

// snippet-start:[ec2.go.release_address.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"
	"github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.release_address.imports]

// ClearAddress releases an Elastic IP address.
// If the address is associated with an Amazon EC2 instance, the association is removed.
// Inputs:
//     svc is an Amazon EC2 service client
//     allocationID is the ID of the Elastic IP address
// Output:
//     If success, nil
//     Otherwise, an error from the call to ReleaseAddress
func ClearAddress(svc ec2iface.EC2API, allocationID *string) error {
	// snippet-start:[ec2.go.release_address.call]
	_, err := svc.ReleaseAddress(&ec2.ReleaseAddressInput{
		AllocationId: allocationID,
	})
	// snippet-end:[ec2.go.release_address.call]

	return err
}

func main() {
	// snippet-start:[ec2.go.release_address.args]
	allocationID := flag.String("a", "", "The ID of an allocated address")
	flag.Parse()

	if *allocationID == "" {
		fmt.Println("You must supply the ID of an allocated address (-a ALLOCATION-ID)")
		return
	}
	// snippet-end:[ec2.go.release_address.args]

	// snippet-start:[ec2.go.release_address.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ec2.New(sess)
	// snippet-end:[ec2.go.release_address.session]

	err := ClearAddress(svc, allocationID)
	if err != nil {
		fmt.Println("Got an error releasing address:")
		fmt.Println(err)
		return
	}

	fmt.Println("Released allocated address")
}
// snippet-end:[ec2.go.release_address]
