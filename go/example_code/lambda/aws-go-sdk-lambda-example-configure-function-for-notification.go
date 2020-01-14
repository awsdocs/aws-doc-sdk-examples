// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Configures a Lambda function to accept notifications from a resource.]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[AddPermission function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
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
// snippet-start:[lambda.go.configure_function.complete]
package main

// snippet-start:[lambda.go.configure_function.imports]
import (
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
)
// snippet-end:[lambda.go.configure_function.imports]

func main() {
    // snippet-start:[lambda.go.configure_function.vars]
    functionPtr := flag.String("f", "", "The name of the Lambda function")
    sourcePtr := flag.String("a", "", "The ARN of the entity invoking the function")
    flag.Parse()

    if *functionPtr == "" || *sourcePtr == "" {
        fmt.Println("You must supply the name of the function and of the entity invoking the function")
        flag.PrintDefaults()
        os.Exit(1)
    }
    // snippet-end:[lambda.go.configure_function.vars]

    // Initialize a session
    // snippet-start:[lambda.go.configure_function.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := lambda.New(sess)
    // snippet-end:[lambda.go.configure_function.session]

    // snippet-start:[lambda.go.configure_function.struct]
    permArgs := &lambda.AddPermissionInput{
        Action:       aws.String("lambda:InvokeFunction"),
        FunctionName: functionPtr,
        Principal:    aws.String("s3.amazonaws.com"),
        SourceArn:    sourcePtr,
        StatementId:  aws.String("lambda_s3_notification"),
    }
    // snippet-end:[lambda.go.configure_function.struct]

    // snippet-start:[lambda.go.configure_function.add_permission]
    result, err := svc.AddPermission(permArgs)
    if err != nil {
        fmt.Println("Cannot configure function for notifications")
        os.Exit(0)
    }

    fmt.Println(result)
    // snippet-end:[lambda.go.configure_function.add_permission]
}
// snippet-end:[lambda.go.configure_function.complete]
