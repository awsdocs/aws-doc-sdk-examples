// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[lambda.go.make_function]
package main

// snippet-start:[lambda.go.make_function.imports]
import (
    "flag"
    "fmt"
    "io/ioutil"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)
// snippet-end:[lambda.go.make_function.imports]

// MakeFunction creates a Lambda function
// Inputs:
//     svc is a Lambda service client
//     zipFile is the name of the zip file
//     bucket is the name of the bucket
//     function is the name of the Lambda function
//     handler is the name of the function that is called in the Lambda function
//     resource is the
//     runtime is the compute runtime environment that runs the Lambda function
// Output:
//     If success, information about the Lambda function and nil
//     Otherwise, nil and an error from the call to ReadFile or CreateFunction
func MakeFunction(svc lambdaiface.LambdaAPI, zipFile, bucket, function, handler, resource, runtime *string) (*lambda.FunctionConfiguration, error) {
    // snippet-start:[lambda.go.make_function.read_file]
    contents, err := ioutil.ReadFile(*zipFile + ".zip")
    // snippet-end:[lambda.go.make_function.read_file]
    if err != nil {
        return nil, err
    }

    // snippet-start:[lambda.go.make_function.structs]
    createCode := &lambda.FunctionCode{
        S3Bucket:        bucket,
        S3Key:           zipFile,
        S3ObjectVersion: aws.String(""),
        ZipFile:         contents,
    }

    createArgs := &lambda.CreateFunctionInput{
        Code:         createCode,
        FunctionName: function,
        Handler:      handler,
        Role:         resource,
        Runtime:      runtime,
    }
    // snippet-end:[lambda.go.make_function.structs]

    // snippet-start:[lambda.go.make_function.call]
    result, err := svc.CreateFunction(createArgs)
    // snippet-end:[lambda.go.make_function.call]
    return result, err
}

func main() {
    // snippet-start:[lambda.go.make_function.args]
    zipFile := flag.String("z", "", "The name of the ZIP file (without the .zip extension)")
    bucket := flag.String("b", "", "the name of bucket to which the ZIP file is uploaded")
    function := flag.String("f", "", "The name of the Lambda function")
    handler := flag.String("h", "", "The name of the package.class handling the call")
    resource := flag.String("a", "", "The ARN of the role that calls the function")
    runtime := flag.String("r", "", "The runtime for the function.")

    flag.Parse()

    if *zipFile == "" || *bucket == "" || *function == "" || *handler == "" || *resource == "" || *runtime == "" {
        fmt.Println("You must supply a zip file name, bucket name, function name, handler, ARN, and runtime.")
        fmt.Println("-z ZIP-FILE -b BUCKET -f FUNCTION-NAME -h HANDLER -a ROLE-ARN -r RUNTIME")
        return
    }
    // snippet-end:[lambda.go.make_function.args]

    // snippet-start:[lambda.go.make_function.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := lambda.New(sess)
    // snippet-end:[lambda.go.make_function.session]

    result, err := MakeFunction(svc, zipFile, bucket, function, handler, resource, runtime)
    if err != nil {
        fmt.Println("Got error creating function:")
        fmt.Println(err)
        return
    }

    // snippet-start:[lambda.go.make_function.display]
    fmt.Println("Function ARN: " + *result.FunctionArn)
    // snippet-end:[lambda.go.make_function.display]
}
// snippet-end:[lambda.go.make_function]
