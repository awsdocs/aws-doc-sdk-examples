// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.delete_security_group.complete]
package main

// snippet-start:[ec2.go.delete_security_group.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"
	"github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)

// snippet-end:[ec2.go.delete_security_group.imports]

// RemoveSecurityGroup deletes a security group.
// Inputs:
//     svc is an Amazon EC2 service client
//     name is the name of the security group
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteSecurityGroup
func RemoveSecurityGroup(svc ec2iface.EC2API, name *string) error {
	// snippet-start:[ec2.go.delete_new_security_group.call]
	_, err := svc.DeleteSecurityGroup(&ec2.DeleteSecurityGroupInput{
		GroupName: name,
	})
	if err != nil {
		return err
	}

	return nil
}

func main() {
	// snippet-start:[ec2.go.delete_security_group.args]
	name := flag.String("n", "", "The name of the security group to delete")
	flag.Parse()

	if *name == "" {
		fmt.Println("You must supply the name of the security group to delete")
		return
	}
	// snippet-end:[ec2.go.delete_security_group.args]

	// snippet-start:[ec2.go.delete_security_group.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := ec2.New(sess)
	// snippet-end:[ec2.go.delete_security_group.session]

	err := RemoveSecurityGroup(svc, name)
	if err != nil {
		fmt.Println("Got an error deleting security group:")
		fmt.Println(err)
		return
	}

	fmt.Println("Deleted security group with ID " + *name)
}

// snippet-end:[ec2.go.delete_security_group.complete]
