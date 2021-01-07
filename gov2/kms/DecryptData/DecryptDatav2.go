// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[kms.go-v2.DecryptData]
package main

import (
	"context"
	b64 "encoding/base64"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/kms"
)

// KMSDecryptAPI defines the interface for the Decrypt function.
// We use this interface to test the function using a mocked service.
type KMSDecryptAPI interface {
	Decrypt(ctx context.Context,
		params *kms.DecryptInput,
		optFns ...func(*kms.Options)) (*kms.DecryptOutput, error)
}

// DecodeData decrypts some text that was encrypted with an AWS Key Management Service (AWS KMS) customer master key (CMK).
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a DecryptOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to Decrypt.
func DecodeData(c context.Context, api KMSDecryptAPI, input *kms.DecryptInput) (*kms.DecryptOutput, error) {
	result, err := api.Decrypt(c, input)

	return result, err
}

func main() {
	data := flag.String("d", "", "The encrypted data, as a string")
	flag.Parse()

	if *data == "" {
		fmt.Println("You must supply the encrypted data as a string")
		fmt.Println("-d DATA")
		return
	}

  cfg, err := config.LoadDefaultConfig(context.TODO())
  if err != nil {
    panic("configuration error, " + err.Error())
  }

	client := kms.NewFromConfig(cfg)

	blob, err := b64.StdEncoding.DecodeString(*data)
	if err != nil {
		panic("error converting string to blob, " + err.Error())
	}

	input := &kms.DecryptInput{
		CiphertextBlob: blob,
	}

	result, err := DecodeData(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got error decrypting data: ", err)
		return
	}

	fmt.Println(string(result.Plaintext))
}
// snippet-end:[kms.go-v2.DecryptData]
