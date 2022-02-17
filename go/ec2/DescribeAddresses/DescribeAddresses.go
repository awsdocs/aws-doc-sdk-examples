// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.describe_addresses]
package main

// snippet-start:[ec2.go.describe_addresses.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)
// snippet-end:[ec2.go.describe_addresses.imports]

// GetAddresses gets the Elastic IP addresses for the account's VPC
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of addresses and nil
//     Otherwise, nil and an error from the call to DescribeAddresses
func GetAddresses(sess *session.Session) (*ec2.DescribeAddressesOutput, error) {
    // snippet-start:[ec2.go.describe_addresses.call]
    svc := ec2.New(sess)

    result, err := svc.DescribeAddresses(&ec2.DescribeAddressesInput{
        Filters: []*ec2.Filter{
            {
                Name:   aws.String("domain"),
                Values: aws.StringSlice([]string{"vpc"}),
            },
        },
    })
    // snippet-end:[ec2.go.describe_addresses.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[ec2.go.describe_addresses.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[ec2.go.describe_addresses.session]

    result, err := GetAddresses(sess)
    if err != nil {
        fmt.Println("Got an error retrieving the Elastic IP addresses")
        return
    }

    // snippet-start:[ec2.go.describe_addresses.display]
    for _, addr := range result.Addresses {
        fmt.Println("IP address:   ", *addr.PublicIp)
        fmt.Println("Allocation ID:", *addr.AllocationId)
        if addr.InstanceId != nil {
            fmt.Println("Instance ID:  ", *addr.InstanceId)
        }
    }
    // snippet-end:[ec2.go.describe_addresses.display]
}
// snippet-end:[ec2.go.describe_addresses]
