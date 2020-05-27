// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.describe_security_groups]
package main

// snippet-start:[ec2.go.describe_security_groups.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)
// snippet-end:[ec2.go.describe_security_groups.imports]

// GetSecurityGroupInfo retrieves information about your Amazon Elastic Compute Cloud (Amazon EC2) security groups.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, a list of the Amazon EC2 security groups and nil
//     Otherwise, nil and an error from the call to DescribeSecurityGroups
func GetSecurityGroupInfo(sess *session.Session) (*ec2.DescribeSecurityGroupsOutput, error) {
    // snippet-start:[ec2.go.describe_security_groups.call]
    svc := ec2.New(sess)

    result, err := svc.DescribeSecurityGroups(nil)
    // snippet-end:[ec2.go.describe_security_groups.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[ec2.go.describe_security_groups.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[ec2.go.describe_security_groups.session]

    result, err := GetSecurityGroupInfo(sess)
    if err != nil {
        fmt.Println("Got an error retrieving information about your security groups:")
        fmt.Println(err)
        return
    }

    // snippet-start:[ec2.go.describe_security_groups.display]
    for _, group := range result.SecurityGroups {
        fmt.Println(*group.GroupName)
        fmt.Println("  Description: " + *group.Description)
        fmt.Println("  Group ID:   " + *group.GroupId)
        fmt.Println("")
    }

    fmt.Println("Found", len(result.SecurityGroups), "security groups")
    // snippet-end:[ec2.go.describe_security_groups.display]
}
// snippet-end:[ec2.go.describe_security_groups]
