// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[kms.go.create_key]
package main

// snippet-start:[kms.go.create_key.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/kms"
    "github.com/aws/aws-sdk-go/service/kms/kmsiface"
)
// snippet-end:[kms.go.create_key.imports]

// MakeKey creates AWS Key Management Service (AWS KMS) customer master key (CMK).
// Inputs:
//     svc is an AWS KMS service client
//     key is the name of the key
//     value is the value of the key
// Output:
//     If success, information about the new key and nil
//     Otherwise, nil and an error from the call to CreateKey
func MakeKey(svc kmsiface.KMSAPI, key, value *string) (*kms.CreateKeyOutput, error) {
    // snippet-start:[kms.go.create_key.call]
    result, err := svc.CreateKey(&kms.CreateKeyInput{
        Tags: []*kms.Tag{
            {
                TagKey:   key,
                TagValue: value,
            },
        },
    })
    // snippet-end:[kms.go.create_key.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[kms.go.create_key.args]
    key := flag.String("k", "", "The KMS key name")
    value := flag.String("v", "", "The value of the KMS key")
    flag.Parse()

    if *key == "" || *value == "" {
        fmt.Println("You must supply a KMS key name and value (-k KEY-NAME -v KEY-VALUE)")
        return
    }
    // snippet-end:[kms.go.create_key.args]

    // snippet-start:[kms.go.create_key.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := kms.New(sess)
    // snippet-end:[kms.go.create_key.session]

    result, err := MakeKey(svc, key, value)
    if err != nil {
        fmt.Println("Got error creating key:")
        fmt.Println(err)
        return
    }

    // snippet-start:[kms.go.create_key.display]
    fmt.Println(*result.KeyMetadata.KeyId)
    // snippet-end:[kms.go.create_key.display]
}
// snippet-end:[kms.go.create_key]
