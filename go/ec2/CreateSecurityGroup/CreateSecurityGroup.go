// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.create_new_security_group.complete]
package main

// snippet-start:[ec2.go.create_new_security_group.imports]
import (
    "errors"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.create_new_security_group.imports]

// MakeSecurityGroup creates a new security group with access to ports 80 and 22 access.
// Inputs:
//     svc is an Amazon EC2 service client
//     name is the name of the security group
//     description is the description of the security group
//     vpcID is the ID of a VPC
// Output:
//     If success, the ID of the VPC, ID of the group, and nil
//     Otherwise, two empty strings and an error from the call to DescribeVpcs, CreateSecurityGroup, or AuthorizeSecurityGroupIngress
func MakeSecurityGroup(svc ec2iface.EC2API, name, description, vpcID *string) (string, string, error) {
    // If the VPC ID wasn't provided in the CLI retrieve the first in the account.
    vID := *vpcID
    gID := ""
    // snippet-start:[ec2.go.create_new_security_group.vpcid]
    if *vpcID == "" {
        // Get a list of VPCs so we can associate the group with the first VPC.
        result, err := svc.DescribeVpcs(nil)
        if err != nil {
            return "", "", err
        }
        if len(result.Vpcs) == 0 {
            return "", "", errors.New("There are no VPCs to associate with")
        }

        vID = *result.Vpcs[0].VpcId
    }
    // snippet-end:[ec2.go.create_new_security_group.vpcid]

    // Create the security group with the VPC, name and description.
    // snippet-start:[ec2.go.create_new_security_group.call]
    result, err := svc.CreateSecurityGroup(&ec2.CreateSecurityGroupInput{
        GroupName:   name,
        Description: description,
        VpcId:       aws.String(vID),
    })
    // snippet-end:[ec2.go.create_new_security_group.call]    
    if err != nil {
        return "", "", err
    }

    gID = *result.GroupId

    // Add permissions to the security group
    // snippet-start:[ec2.go.create_new_security_group.permissions]
    _, err = svc.AuthorizeSecurityGroupIngress(&ec2.AuthorizeSecurityGroupIngressInput{
        GroupName: name,
        IpPermissions: []*ec2.IpPermission{
            // Can use setters to simplify seting multiple values without the
            // needing to use aws.String or associated helper utilities.
            (&ec2.IpPermission{}).
                SetIpProtocol("tcp").
                SetFromPort(80).
                SetToPort(80).
                SetIpRanges([]*ec2.IpRange{
                    {CidrIp: aws.String("0.0.0.0/0")},
                }),
            (&ec2.IpPermission{}).
                SetIpProtocol("tcp").
                SetFromPort(22).
                SetToPort(22).
                SetIpRanges([]*ec2.IpRange{
                    (&ec2.IpRange{}).
                        SetCidrIp("0.0.0.0/0"),
                }),
        },
    })
    // snippet-end:[ec2.go.create_new_security_group.permissions]
    if err != nil {
        return "", "", err
    }

    return vID, gID, nil
}

func main() {
    // snippet-start:[ec2.go.create_new_security_group.args]
    name := flag.String("n", "", "Group Name")
    description := flag.String("d", "", "Group Description")
    vpcID := flag.String("vpc", "", "(Optional) VPC ID to associate security group with")

    flag.Parse()

    if *name == "" || *description == "" {
        fmt.Println("You must supply a group name and description")
        fmt.Println("-n GROUP-NAME -d GROUP-DESCRIPTION [-v VPC-ID]")
        return
    }
    // snippet-end:[ec2.go.create_new_security_group.args]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    // snippet-start:[ec2.go.create_new_security_group.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.create_new_security_group.session]

    vID, gID, err := MakeSecurityGroup(svc, name, description, vpcID)
    if err != nil {
        fmt.Println("Got an error creating security group:")
        fmt.Println(err)
        return
    }

    fmt.Println("Created security group with ID " + gID + " with VPC with ID " + vID)
}
// snippet-end:[ec2.go.create_new_security_group.complete]
