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
// snippet-sourcedate:[2020-1-6]
/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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

// snippet-start:[lambda.go.create_function.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"

    "flag"
    "fmt"
    "io/ioutil"
    "os"
)
// snippet-end:[lambda.go.create_function.imports]

func main() {
    // snippet-start:[lambda.go.create_function.vars]
    zipFilePtr := flag.String("z", "", "The name of the ZIP file (without the .zip extension)")
    bucketPtr := flag.String("b", "", "the name of bucket to which the ZIP file is uploaded")
    functionPtr := flag.String("f", "", "The name of the Lambda function")
    handlerPtr := flag.String("h", "", "The name of the package.class handling the call")
    resourcePtr := flag.String("a", "", "The ARN of the role that calls the function")
    runtimePtr := flag.String("r", "", "The runtime for the function.")

    flag.Parse()

    if *zipFilePtr == "" || *bucketPtr == "" || *functionPtr == "" || *handlerPtr == "" || *resourcePtr == "" || *runtimePtr == "" {
        fmt.Println("You must supply a zip file name, bucket name, function name, handler, ARN, and runtime.")
        os.Exit(0)
    }
    // snippet-end:[lambda.go.create_function.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    // snippet-start:[lambda.go.create_function.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := lambda.New(sess)

    contents, err := ioutil.ReadFile(*zipFilePtr + ".zip")
    if err != nil {
        fmt.Println("Could not read " + *zipFilePtr + ".zip")
        os.Exit(0)
    }
    // snippet-end:[lambda.go.create_function.session]

    // snippet-start:[lambda.go.create_function.structs]
    createCode := &lambda.FunctionCode{
        S3Bucket:        bucketPtr,
        S3Key:           zipFilePtr,
        S3ObjectVersion: aws.String(""),
        ZipFile:         contents,
    }

    createArgs := &lambda.CreateFunctionInput{
        Code:         createCode,
        FunctionName: functionPtr,
        Handler:      handlerPtr,
        Role:         resourcePtr,
        Runtime:      runtimePtr,
    }
    // snippet-end:[lambda.go.create_function.structs]

    // snippet-start:[lambda.go.create_function.create]
    result, err := svc.CreateFunction(createArgs)
    if err != nil {
        fmt.Println("Cannot create function: " + err.Error())
    } else {
        fmt.Println(result)
    }
    // snippet-end:[lambda.go.create_function.create]
}
// snippet-end:[lambda.go.create_function.complete]
