// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[kms-go-v2.CreateKey]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/kms"
    "github.com/aws/aws-sdk-go-v2/service/kms/types"
)

// KMSCreateKeyAPI defines the interface for the CreateKey function.
// We use this interface to test the function using a mocked service.
type KMSCreateKeyAPI interface {
    CreateKey(ctx context.Context,
        params *kms.CreateKeyInput,
        optFns ...func(*kms.Options)) (*kms.CreateKeyOutput, error)
}

// MakeKey creates an AWS Key Management Service (AWS KMS) customer master key (CMK).
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a CreateKeyOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateKey.
func MakeKey(c context.Context, api KMSCreateKeyAPI, input *kms.CreateKeyInput) (*kms.CreateKeyOutput, error) {
    result, err := api.CreateKey(c, input)

    return result, err
}

func main() {
    key := flag.String("k", "", "The KMS key name")
    value := flag.String("v", "", "The value of the KMS key")
    flag.Parse()

    if *key == "" || *value == "" {
        fmt.Println("You must supply a KMS key name and value (-k KEY-NAME -v KEY-VALUE)")
        return
    }

    cfg, err := config.LoadDefaultConfig()
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := kms.NewFromConfig(cfg)

    input := &kms.CreateKeyInput{
        Tags: []*types.Tag{
            {
                TagKey:   key,
                TagValue: value,
            },
        },
    }

    result, err := MakeKey(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got error creating key:")
        fmt.Println(err)
        return
    }

    fmt.Println(*result.KeyMetadata.KeyId)
}

// snippet-end:[kms-go-v2.CreateKey]
