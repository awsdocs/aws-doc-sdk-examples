// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[lambda.go.show_functions]
package main

// snippet-start:[lambda.go.show_functions.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)
// snippet-end:[lambda.go.show_functions.imports]

// GetFunctions retrieves a list of AWS Lambda functions
// Inputs:
//     svc is a Lambda service client
// Output:
//     If success, the list of functions and nil
//     Otherwise, nil and an error from the call to ListFunctions
func GetFunctions(svc lambdaiface.LambdaAPI) (*lambda.ListFunctionsOutput, error) {
    // snippet-start:[lambda.go.show_functions.call]
    result, err := svc.ListFunctions(nil)
    // snippet-end:[lambda.go.show_functions.call]
    return result, err
}

func main() {
    // snippet-start:[lambda.go.show_functions.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := lambda.New(sess)
    // snippet-end:[lambda.go.show_functions.session]

    result, err := GetFunctions(svc)
    if err != nil {
        fmt.Println("Got an error listing functions:")
        fmt.Println(err)
        return
    }

    // snippet-start:[lambda.go.show_functions.display]
    fmt.Println("Functions:")

    for _, f := range result.Functions {
        fmt.Println("Name:        " + aws.StringValue(f.FunctionName))
        fmt.Println("Description: " + aws.StringValue(f.Description))
        fmt.Println("")
    }
    // snippet-end:[lambda.go.show_functions.display]
}
// snippet-end:[lambda.go.show_functions]
