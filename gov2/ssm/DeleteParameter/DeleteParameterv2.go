// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[ssm.go-v2.DeleteParameter]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/ssm"
)

// SSMDeleteParameterAPI defines the interface for the DeleteParameter function.
// We use this interface to test the function using a mocked service.
type SSMDeleteParameterAPI interface {
    DeleteParameter(ctx context.Context,
        params *ssm.DeleteParameterInput,
        optFns ...func(*ssm.Options)) (*ssm.DeleteParameterOutput, error)
}

// RemoveParameter deletes an AWS Systems Manager string parameter.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to DeleteParameter.
func RemoveParameter(c context.Context, api SSMDeleteParameterAPI, input *ssm.DeleteParameterInput) (*ssm.DeleteParameterOutput, error) {
    results, err := api.DeleteParameter(c, input)

    return results, err
}

func main() {
    parameterName := flag.String("n", "", "The name of the parameter")
    flag.Parse()

    if *parameterName == "" {
        fmt.Println("You must supply the name of the parameter")
        fmt.Println("-n NAME")
        return
    }

    cfg, err := config.LoadDefaultConfig()
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := ssm.NewFromConfig(cfg)

    input := &ssm.DeleteParameterInput{
        Name: parameterName,
    }

    _, err = RemoveParameter(context.Background(), client, input)
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Deleted parameter " + *parameterName)
}
// snippet-end:[ssm.go-v2.DeleteParameter]
