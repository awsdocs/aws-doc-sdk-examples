// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Creates a Lambda function.]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[CreateFunction function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-keyword:[Code Sample]
// snippet-service:[lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-1-11]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[lambda.go.create_function.complete]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"

    "flag"
    "fmt"
    "io/ioutil"
    "os"
)

func createFunction(zipFileName string, bucketName string, functionName string, handler string, resourceArn string, runtime string) {
    // Initialize a session
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Lambda service client
    svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    contents, err := ioutil.ReadFile(zipFileName + ".zip")

    if err != nil {
        fmt.Println("Could not read " + zipFileName + ".zip")
        os.Exit(0)
    }

    createCode := &lambda.FunctionCode{
        S3Bucket:        aws.String(bucketName),
        S3Key:           aws.String(zipFileName),
        S3ObjectVersion: aws.String(""),
        ZipFile:         contents,
    }

    createArgs := &lambda.CreateFunctionInput{
        Code:         createCode,
        FunctionName: aws.String(functionName),
        Handler:      aws.String(handler),
        Role:         aws.String(resourceArn),
        Runtime:      aws.String(runtime),
    }

    result, err := svc.CreateFunction(createArgs)
    if err != nil {
        fmt.Println("Cannot create function: " + err.Error())
    } else {
        fmt.Println(result)
    }
}

func main() {
    zipFilePtr := flag.String("z", "", "The name of the ZIP file (without the .zip extension)")
    bucketPtr := flag.String("b", "", "the name of bucket to which the ZIP file is uploaded")
    functionPtr := flag.String("f", "", "The name of the Lambda function")
    handlerPtr := flag.String("h", "", "The name of the package.class handling the call")
    resourcePtr := flag.String("a", "", "The ARN of the role that calls the function")
    runtimePtr := flag.String("r", "", "The runtime for the function.")

    flag.Parse()

    zipFile := *zipFilePtr
    bucketName := *bucketPtr
    functionName := *functionPtr
    handler := *handlerPtr
    resourceArn := *resourcePtr
    runtime := *runtimePtr

    if zipFile == "" || bucketName == "" || functionName == "" || handler == "" || resourceArn == "" || runtime == "" {
        fmt.Println("You must supply a zip file name, bucket name, function name, handler, ARN, and runtime.")
        os.Exit(0)
    }

    createFunction(zipFile, bucketName, functionName, handler, resourceArn, runtime)
}
// snippet-end:[lambda.go.create_function.complete]