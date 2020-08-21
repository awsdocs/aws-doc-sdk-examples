// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.describe_keypairs]
package main

// snippet-start:[ec2.go.describe_keypairs.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)
// snippet-end:[ec2.go.describe_keypairs.imports]

// GetKeyPairs retrieves a list of key pairs stored in Amazon EC2.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of key pairs and nil
//     Otherwise, nil and an error from the call to DescribeKeyPairs
func GetKeyPairs(sess *session.Session) (*ec2.DescribeKeyPairsOutput, error) {
    // snippet-start:[ec2.go.describe_keypairs.call]
    svc := ec2.New(sess)

    result, err := svc.DescribeKeyPairs(nil)
    // snippet-end:[ec2.go.describe_keypairs.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[ec2.go.describe_keypairs.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[ec2.go.describe_keypairs.session]

    result, err := GetKeyPairs(sess)
    if err != nil {
        fmt.Println("Got an error retrieving key pairs:")
        fmt.Println(err)
        return
    }

    // snippet-start:[ec2.go.describe_keypairs.display]
    fmt.Println("Key Pairs:")
    for _, pair := range result.KeyPairs {
        fmt.Println("  Name:        " + *pair.KeyName)
        fmt.Println("  Fingerprint: " + *pair.KeyFingerprint)
        fmt.Println("")
    }
    // snippet-end:[ec2.go.describe_keypairs.display]
}
// snippet-end:[ec2.go.describe_keypairs]
