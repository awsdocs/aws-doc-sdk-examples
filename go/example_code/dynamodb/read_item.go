// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Gets an item from an Amazon DynamoDB table.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[GetItem function]
// snippet-keyword:[Go]
// snippet-service:[dynamodb]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
)

// Create structs to hold info about new item
type ItemInfo struct {
    Plot string`json:"plot"`
    Rating float64`json:"rating"`
}

type Item struct {
    Year int`json:"year"`
    Title string`json:"title"`
    Info ItemInfo`json:"info"`
}

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create DynamoDB client
    svc := dynamodb.New(sess)

    result, err := svc.GetItem(&dynamodb.GetItemInput{
        TableName: aws.String("Movies"),
        Key: map[string]*dynamodb.AttributeValue{
            "year": {
                N: aws.String("2015"),
            },
            "title": {
                S: aws.String("The Big New Movie"),
            },
        },
    })

    if err != nil {
        fmt.Println(err.Error())
        return
    }

    item := Item{}

    err = dynamodbattribute.UnmarshalMap(result.Item, &item)

    if err != nil {
        panic(fmt.Sprintf("Failed to unmarshal Record, %v", err))
    }

    if item.Title == "" {
        fmt.Println("Could not find 'The Big New Movie' (2015)")
        return
    }

    fmt.Println("Found item:")
    fmt.Println("Year:  ", item.Year)
    fmt.Println("Title: ", item.Title)
    fmt.Println("Plot:  ", item.Info.Plot)
    fmt.Println("Rating:", item.Info.Rating)
}
