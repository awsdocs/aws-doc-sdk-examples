// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[kms.go.reencrypt_data]
package main

// snippet-start:[kms.go.reencrypt_data.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/kms"
    "github.com/aws/aws-sdk-go/service/kms/kmsiface"
)
// snippet-end:[kms.go.reencrypt_data.imports]

// ReEncryptText reencrypts some text using a new AWS Key Management Service (AWS KMS) customer master key (CMK).
// Inputs:
//     svc is an AWS KMS service client
//     keyID is the ID of a different CMK
// Output:
//     If success, information about the reencrypted text and nil
//     Otherwise, nil and an error from the call to ReEncrypt
func ReEncryptText(svc kmsiface.KMSAPI, blob *[]byte, keyID *string) (*kms.ReEncryptOutput, error) {
    // snippet-start:[kms.go.reencrypt_data.call]
    result, err := svc.ReEncrypt(&kms.ReEncryptInput{
        CiphertextBlob:   *blob,
        DestinationKeyId: keyID,
    })
    // snippet-end:[kms.go.reencrypt_data.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[kms.go.reencrypt_data.args]
    keyID := flag.String("k", "", "The ID of a KMS key")
    data := flag.String("d", "", "The data to reencrypt, as a string")
    flag.Parse()

    if *keyID == "" || *data == "" {
        fmt.Println("You must supply the ID of a KMS key and data")
        fmt.Println("-k KEY-ID -d DATA")
        return
    }
    // snippet-end:[kms.go.reencrypt_data.args]

    // snippet-start:[kms.go.reencrypt_data.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := kms.New(sess)

    blob := []byte(*data)
    // snippet-end:[kms.go.reencrypt_data.session]

    result, err := ReEncryptText(svc, &blob, keyID)
    if err != nil {
        fmt.Println("Got error reencrypting data:")
        fmt.Println(err)
        return
    }

    // snippet-start:[kms.go.reencrypt_data.display]
    fmt.Println("Blob (base-64 byte array):")
    fmt.Println(result.CiphertextBlob)
    // snippet-end:[kms.go.reencrypt_data.display]
}
// snippet-end:[kms.go.reencrypt_data]
