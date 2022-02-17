// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[lambda.go.config_function]
package main

// snippet-start:[lambda.go.config_function.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)
// snippet-end:[lambda.go.config_function.imports]

// AddPerm enables an Amazon S3 bucket to send notifications to an AWS Lambda function.
// Inputs:
//     svc is a Lambda service client
//     function is the name of the Lambda function
//     s3ARN is the Amazon Resource Name (ARN) of the Amazon S3 bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to AddPermission
func AddPerm(svc lambdaiface.LambdaAPI, function, s3ARN *string) error {
    // snippet-start:[lambda.go.config_function.call]
    _, err := svc.AddPermission(&lambda.AddPermissionInput{
        Action:       aws.String("lambda:InvokeFunction"),
        FunctionName: function,
        Principal:    aws.String("s3.amazonaws.com"),
        SourceArn:    s3ARN,
        StatementId:  aws.String("lambda_s3_notification"),
    })
    // snippet-end:[lambda.go.config_function.call]
    return err
}

func main() {
    // snippet-start:[lambda.go.config_function.args]
    function := flag.String("f", "", "The name of the Lambda function")
    s3ARN := flag.String("a", "", "The ARN of the Amazon S3 bucket sending a notification to the function")
    flag.Parse()

    if *function == "" || *s3ARN == "" {
        fmt.Println("You must supply the name of a Lambda function and the ARN of an S3 bucket")
        fmt.Println("-f FUNCTION -a ARN")
        return
    }
    // snippet-end:[lambda.go.config_function.args]

    // snippet-start:[lambda.go.config_function.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := lambda.New(sess)
    // snippet-end:[lambda.go.config_function.session]

    err := AddPerm(svc, function, s3ARN)
    if err != nil {
        fmt.Println("Got an error configuring the function for notifications:")
        fmt.Println(err)
        return
    }

    fmt.Println("Configured the function for notifications")
}
// snippet-end:[lambda.go.config_function]
