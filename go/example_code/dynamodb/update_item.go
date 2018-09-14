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

    // Create item in table Movies
    input := &dynamodb.UpdateItemInput{
        ExpressionAttributeValues: map[string]*dynamodb.AttributeValue{
            ":r": {
                N: aws.String("0.5"),
            },
        },
        TableName: aws.String("Movies"),
        Key: map[string]*dynamodb.AttributeValue{
            "year": {
                N: aws.String("2015"),
            },
            "title": {
                S: aws.String("The Big New Movie"),
            },
        },
        ReturnValues:     aws.String("UPDATED_NEW"),
        UpdateExpression: aws.String("set info.rating = :r"),
    }

    _, err = svc.UpdateItem(input)

    if err != nil {
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Successfully updated 'The Big New Movie' (2015) rating to 0.5")
}
