// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[kms.go-v2.EncryptData]
package main

import (
	"context"
	b64 "encoding/base64"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/kms"
)

// KMSEncryptAPI defines the interface for the Encrypt function.
// We use this interface to test the function using a mocked service.
type KMSEncryptAPI interface {
	Encrypt(ctx context.Context,
		params *kms.EncryptInput,
		optFns ...func(*kms.Options)) (*kms.EncryptOutput, error)
}

// EncryptText encrypts some text using an AWS Key Management Service (AWS KMS) customer master key (CMK).
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, an EncryptOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to Encrypt.
func EncryptText(c context.Context, api KMSEncryptAPI, input *kms.EncryptInput) (*kms.EncryptOutput, error) {
	result, err := api.Encrypt(c, input)

	return result, err
}

func main() {
	keyID := flag.String("k", "", "The ID of a KMS key")
	text := flag.String("t", "", "The text to encrypt")
	flag.Parse()

	if *keyID == "" || *text == "" {
		fmt.Println("You must supply the ID of a KMS key and some text")
		fmt.Println("-k KEY-ID -t \"text\"")
		return
	}

  cfg, err := config.LoadDefaultConfig(context.TODO())
  if err != nil {
    panic("configuration error, " + err.Error())
  }

	client := kms.NewFromConfig(cfg)

	input := &kms.EncryptInput{
		KeyId:     keyID,
		Plaintext: []byte(*text),
	}

	result, err := EncryptText(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got error encrypting data:")
		fmt.Println(err)
		return
	}

	blobString := b64.StdEncoding.EncodeToString([]byte(result.CiphertextBlob))

	fmt.Println(blobString)
}
// snippet-end:[kms.go-v2.EncryptData]
