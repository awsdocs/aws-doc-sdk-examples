// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.delete_key_pair]
package main

// snippet-start:[ec2.go.delete_key_pair.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.delete_key_pair.imports]

// RemoveKeyPair deletes a new Amazon Elastic Compute Cloud (Amazon EC2) key pair.
// Inputs:
//     svc is an Amazon EC2 service client
//     keyName is the name of the key pair
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteKeyPair
func RemoveKeyPair(svc ec2iface.EC2API, keyName *string) error {
    // snippet-start:[ec2.go.delete_key_pair.call]
    _, err := svc.DeleteKeyPair(&ec2.DeleteKeyPairInput{
        KeyName: keyName,
    })
    // snippet-end:[ec2.go.delete_key_pair.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[ec2.go.delete_key_pair.args]
    keyName := flag.String("k", "", "The name of the key pair")
    flag.Parse()

    if *keyName == "" {
        fmt.Println("You must supply the name of the key pair (-k KEY-NAME)")
        return
    }
    // snippet-end:[ec2.go.delete_key_pair.args]

    // snippet-start:[ec2.go.delete_key_pair.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.delete_key_pair.session]

    // Deletes a key pair with the given name
    err := RemoveKeyPair(svc, keyName)
    if err != nil {
        fmt.Println("Got an error deleting the key pair:")
        fmt.Println(err)
        return
    }

    // snippet-start:[ec2.go.delete_key_pair.display]
    fmt.Println("Deleted key pair")
    // snippet-end:[ec2.go.delete_key_pair.display]
}
// snippet-end:[ec2.go.delete_key_pair]
