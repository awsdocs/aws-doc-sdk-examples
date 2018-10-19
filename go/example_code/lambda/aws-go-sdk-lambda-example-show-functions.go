//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Lists your Lambda functions.]
//snippet-keyword:[AWS Lambda]
//snippet-keyword:[ListFunctions function]
//snippet-keyword:[Go]
//snippet-service:[lambda]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
 Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

 This file is licensed under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the License. A copy of the
 License is located at

 http://aws.amazon.com/apache2.0/

 This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
*/

package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"

    "fmt"
    "os"
)

// Lists all of your Lambda functions in us-west-2
func main() {
    // Initialize a session
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Lambda service client
    svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    result, err := svc.ListFunctions(nil)
    if err != nil {
        fmt.Println("Cannot list functions")
        os.Exit(0)
    }

    fmt.Println("Functions:")

    for _, f := range result.Functions {
        fmt.Println("Name:        " + aws.StringValue(f.FunctionName))
        fmt.Println("Description: " + aws.StringValue(f.Description))
        fmt.Println("")
    }
}
