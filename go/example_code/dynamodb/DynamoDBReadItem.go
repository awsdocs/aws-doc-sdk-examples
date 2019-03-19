// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[DynamoDBReadItem.go gets an item from an Amazon DynamoDB table.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[GetItem function]
// snippet-keyword:[Go]
// snippet-service:[dynamodb]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-19]
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
// snippet-start:[dynamodb.go.read_item]
package main

// snippet-start:[dynamodb.go.read_item.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"

    "fmt"
)
// snippet-end:[dynamodb.go.read_item.imports]

// snippet-start:[dynamodb.go.read_item.struct]
// Create struct to hold info about new item
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
// snippet-end:[dynamodb.go.read_item.struct]

func main() {
    // snippet-start:[dynamodb.go.read_item.session]
    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and region from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.read_item.session]

    // snippet-start:[dynamodb.go.read_item.call]
    tableName := "Movies"
    movieName := "The Big New Movie"
    movieYear := "2015"

    result, err := svc.GetItem(&dynamodb.GetItemInput{
        TableName: aws.String(tableName),
        Key: map[string]*dynamodb.AttributeValue{
            "Year": {
                N: aws.String(movieYear),
            },
            "Title": {
                S: aws.String(movieName),
            },
        },
    })
    if err != nil {
        fmt.Println(err.Error())
        return
    }
    // snippet-end:[dynamodb.go.read_item.call]

    // snippet-start:[dynamodb.go.read_item.unmarshall]
    item := Item{}

    err = dynamodbattribute.UnmarshalMap(result.Item, &item)
    if err != nil {
        panic(fmt.Sprintf("Failed to unmarshal Record, %v", err))
    }

    if item.Title == "" {
        fmt.Println("Could not find '" + movieName + "' (" + movieYear + ")")
        return
    }

    fmt.Println("Found item:")
    fmt.Println("Year:  ", item.Year)
    fmt.Println("Title: ", item.Title)
    fmt.Println("Plot:  ", item.Plot)
    fmt.Println("Rating:", item.Rating)
    // snippet-end:[dynamodb.go.read_item.unmarshall]
}
// snippet-end:[dynamodb.go.read_item]
