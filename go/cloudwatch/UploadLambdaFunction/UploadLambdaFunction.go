/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "flag"
    "fmt"
    "io/ioutil"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
)
// snippet-end:[lambda.go.create_function.imports]

// UploadFunction uploads a Lambda function to a bucket.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients.
//     zipFile is the name of the ZIP file, without the .zip, to upload.
//     bucket is the name of the Amazon S3 bucket.
//     function is the name of the Lambda function.
//     handler is the name of the method within the code that Lambda calls to execute the function.
//     role is the ARN of the function's execution role.
//     runtime is the name of the runtime for the function code.
//         For a function written in Go, this is "go1.x".
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
func UploadFunction(sess *session.Session, zipFile *string, bucket *string, function *string, handler *string, role *string, runtime *string) (*lambda.FunctionConfiguration, error) {
    // Create service client
    // snippet-start:[lambda.go.create_function.client]
    svc := lambda.New(sess)
    // snippet-end:[lambda.go.create_function.client]

    // snippet-start:[lambda.go.create_function.read_zip]
    contents, err := ioutil.ReadFile(*zipFile + ".zip")
    // snippet-end:[lambda.go.create_function.read_zip]
    if err != nil {
        fmt.Println("Got error trying to read " + *zipFile + ".zip")
        return nil, err
    }

    // snippet-start:[lambda.go.create_function.structs]
    createCode := &lambda.FunctionCode{
        //      S3Bucket:        bucket,
        //      S3Key:           zipFile,
        //      S3ObjectVersion: aws.String("1"),
        ZipFile: contents,
    }

    createArgs := &lambda.CreateFunctionInput{
        Code:         createCode,
        FunctionName: function,
        Handler:      handler,
        Role:         role,
        Runtime:      runtime,
    }
    // snippet-end:[lambda.go.create_function.structs]

    // snippet-start:[lambda.go.create_function.create]
    result, err := svc.CreateFunction(createArgs)
    // snippet-end:[lambda.go.create_function.create]
    if err != nil {
        fmt.Println("Cannot create function")
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[lambda.go.create_function.vars]
    zipFile := flag.String("z", "", "The name of the ZIP file, without the .zip extension.")
    bucket := flag.String("b", "", "the name of bucket to which the ZIP file is uploaded.")
    function := flag.String("f", "", "The name of the Lambda function.")
    handler := flag.String("h", "main", "The name of the package.class handling the call.")
    roleARN := flag.String("a", "", "The ARN of the role that calls the function.")
    runtime := flag.String("r", "go1.x", "The runtime for the function.")

    flag.Parse()

    if *zipFile == "" || *bucket == "" || *function == "" || *handler == "" || *roleARN == "" || *runtime == "" {
        fmt.Println("You must supply a zip file name, bucket name, function name, handler (package) name, role ARN, and runtime value.")
        return
    }
    // snippet-end:[lambda.go.create_function.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    // snippet-start:[lambda.go.create_function.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[lambda.go.create_function.session]

    result, err := UploadFunction(sess, zipFile, bucket, function, handler, roleARN, runtime)
    if err != nil {
        fmt.Println(err)
        return
    }

    fmt.Println("Lambda function ARN: " + *result.FunctionArn)
}
// snippet-end:[lambda.go.create_function.complete]
