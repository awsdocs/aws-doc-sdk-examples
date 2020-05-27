// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[kms.go.decrypt_data]
package main

// snippet-start:[kms.go.decrypt_data.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/kms"
    "github.com/aws/aws-sdk-go/service/kms/kmsiface"
)
// snippet-end:[kms.go.decrypt_data.imports]

// DecodeData decrypts some text that was encrypted with an AWS Key Management Service (AWS KMS) customer master key (CMK).
// Inputs:
//     svc is an AWS KMS service client
//     blob is an array of bytes containing the text to decrypt
// Output:
//     If success, information about the text and nil
//     Otherwise, nil and an error from the call to Decrypt
func DecodeData(svc kmsiface.KMSAPI, blob *[]byte) (*kms.DecryptOutput, error) {
    // snippet-start:[kms.go.decrypt_data.call]
    result, err := svc.Decrypt(&kms.DecryptInput{
        CiphertextBlob: *blob,
    })
    // snippet-end:[kms.go.decrypt_data.call]
    if err != nil {
        return nil, err
    }

    return result, err
}

func main() {
    // snippet-start:[kms.go.decrypt_data.args]
    data := flag.String("d", "", "The encrypted data, as a string")
    flag.Parse()

    if *data == "" {
        fmt.Println("You must supply the encrypted data as a string")
        fmt.Println("-d DATA")
        return
    }
    // snippet-end:[kms.go.decrypt_data.args]

    // snippet-start:[kms.go.decrypt_data.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := kms.New(sess)

    blob := []byte(*data)
    // snippet-end:[kms.go.decrypt_data.session]

    result, err := DecodeData(svc, &blob)
    if err != nil {
        fmt.Println("Got error decrypting data: ", err)
        return
    }

    // snippet-start:[kms.go.decrypt_data.display]
    fmt.Println(string(result.Plaintext))
    // snippet-end:[kms.go.decrypt_data.display]
}
// snippet-end:[kms.go.decrypt_data]
