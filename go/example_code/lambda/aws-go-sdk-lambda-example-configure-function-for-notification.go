//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Configures a Lambda function to accept notifications from a resource.]
//snippet-keyword:[AWS Lambda]
//snippet-keyword:[AddPermission function]
//snippet-keyword:[Go]
//snippet-service:[lambda]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
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

package main

import (
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
)

func addNotification(functionName *string, sourceArn *string) {
    // Initialize a session
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Lambda service client
    svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    permArgs := &lambda.AddPermissionInput{
        Action:       aws.String("lambda:InvokeFunction"),
        FunctionName: functionName,
        Principal:    aws.String("s3.amazonaws.com"),
        SourceArn:    sourceArn,
        StatementId:  aws.String("lambda_s3_notification"),
    }

    result, err := svc.AddPermission(permArgs)

    if err != nil {
        fmt.Println("Cannot configure function for notifications")
        os.Exit(0)
    } else {
        fmt.Println(result)
    }
}

func main() {
    flag.String(&functionName, "f", "", "The name of the Lambda function")
    flag.String(&sourceArn, "a", "", "The ARN of the entity invoking the function")
    flag.Parse()

    addNotification(functionName, sourceArn)
}
