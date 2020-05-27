// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.create_key_pair]
package main

// snippet-start:[ec2.go.create_key_pair.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.create_key_pair.imports]

// MakeKeyPair creates a Amazon Elastic Compute Cloud (Amazon EC2) key pair.
// Inputs:
//     svc is an Amazon EC2 service client
//     keyName is the name of the key pair
// Output:
//     If success, information about the key pair and nil
//     Otherwise, nil and an error from the call to CreateKeyPair
func MakeKeyPair(svc ec2iface.EC2API, keyName *string) (*ec2.CreateKeyPairOutput, error) {
    // snippet-start:[ec2.go.create_key_pair.call]
    result, err := svc.CreateKeyPair(&ec2.CreateKeyPairInput{
        KeyName: keyName,
    })
    // snippet-end:[ec2.go.create_key_pair.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[ec2.go.create_key_pair.args]
    keyName := flag.String("k", "", "The name of the key pair")
    flag.Parse()

    if *keyName == "" {
        fmt.Println("You must supply the name of the key pair (-k KEY-NAME)")
        return
    }
    // snippet-end:[ec2.go.create_key_pair.args]

    // snippet-start:[ec2.go.create_key_pair.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.create_key_pair.session]

    // Creates a key pair with the given name
    result, err := MakeKeyPair(svc, keyName)
    if err != nil {
        fmt.Println("Got an error creating the key pair:")
        fmt.Println(err)
        return
    }

    // snippet-start:[ec2.go.create_key_pair.display]
    fmt.Println("Created key pair:")
    fmt.Println("  Name:        " + *result.KeyName)
    fmt.Println("  Fingerprint: " + *result.KeyFingerprint)
    fmt.Println("  Material:    " + *result.KeyMaterial)
    // snippet-end:[ec2.go.create_key_pair.display]
}
// snippet-end:[ec2.go.create_key_pair]
