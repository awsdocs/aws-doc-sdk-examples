// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.allocate_address]
package main

// snippet-start:[ec2.go.allocate_address.import]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.allocate_address.import]

// AllocateAndAssociate allocates a VPC Elastic IP address with an instance.
// Inputs:
//     svc is an Amazon EC2 service client
//     instanceID is the ID of the instance
// Output:
//     If success, information about the allocation and association and nil
//     Otherwise, two nils and an error from the call to AllocateAddress or AssociateAddress
func AllocateAndAssociate(svc ec2iface.EC2API, instanceID *string) (*ec2.AllocateAddressOutput, *ec2.AssociateAddressOutput, error) {
    // snippet-start:[ec2.go.allocate_address.allocate]
    allocRes, err := svc.AllocateAddress(&ec2.AllocateAddressInput{
        Domain: aws.String("vpc"),
    })
    // snippet-end:[ec2.go.allocate_address.allocate]
    if err != nil {
        return nil, nil, err
    }

    // snippet-start:[ec2.go.allocate_address.associate]
    assocRes, err := svc.AssociateAddress(&ec2.AssociateAddressInput{
        AllocationId: allocRes.AllocationId,
        InstanceId:   instanceID,
    })
    // snippet-end:[ec2.go.allocate_address.associate]
    if err != nil {
        return nil, nil, err
    }

    return allocRes, assocRes, nil
}

func main() {
    // snippet-start:[ec2.go.allocate_address.args]
    instanceID := flag.String("i", "", "The ID of the instance")
    flag.Parse()

    if *instanceID == "" {
        fmt.Println("You must supply the ID of an instance")
        return
    }
    // snippet-end:[ec2.go.allocate_address.args]

    // snippet-start:[ec2.go.allocate_address.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.allocate_address.session]

    allocRes, assocRes, err := AllocateAndAssociate(svc, instanceID)
    if err != nil {
        fmt.Println("Got an error attempting to associate IP address to instance with ID "+*instanceID, ":")
        fmt.Println(err)
        return
    }

    // snippet-start:[ec2.go.allocate_address.display]
    fmt.Println("Allocated IP address", *allocRes.PublicIp, "with instance", *instanceID)
    fmt.Println("Allocation id:", *allocRes.AllocationId, "association id: ", *assocRes.AssociationId)
    // snippet-end:[ec2.go.allocate_address.display]
}
// snippet-end:[ec2.go.allocate_address]
