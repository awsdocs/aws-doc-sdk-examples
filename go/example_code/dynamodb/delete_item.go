//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Deletes an item from an Amazon DynamoDB table.]
//snippet-keyword:[Amazon DynamoDB]
//snippet-keyword:[DeleteItem function]
//snippet-keyword:[Go]
//snippet-service:[dynamodb]
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
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
)

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create DynamoDB client
    svc := dynamodb.New(sess)

    input := &dynamodb.DeleteItemInput{
        Key: map[string]*dynamodb.AttributeValue{
            "year": {
                N: aws.String("2015"),
            },
            "title": {
                S: aws.String("The Big New Movie"),
            },
        },
        TableName: aws.String("Movies"),
    }

    _, err = svc.DeleteItem(input)

    if err != nil {
        fmt.Println("Got error calling DeleteItem")
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Deleted 'The Big New Movie' (2015)")
}
