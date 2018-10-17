//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Adds items from a JSON file to an Amazon DynamoDB table.]
//snippet-keyword:[Amazon DynamoDB]
//snippet-keyword:[PutItem function]
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
    "encoding/json"
    "fmt"
    "io/ioutil"
    "os"

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

// Get table items from JSON file
func getItems() []Item {
    raw, err := ioutil.ReadFile("./movie_data.json")

    if err != nil {
        fmt.Println(err.Error())
        os.Exit(1)
    }

    var items []Item
    json.Unmarshal(raw, &items)
    return items
}

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    if err != nil {
        fmt.Println("Error creating session:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Create DynamoDB client
    svc := dynamodb.New(sess)

    // Get table items from movie_data.json
    items := getItems()

    // Add each item to Movies table:
    for _, item := range items {
        av, err := dynamodbattribute.MarshalMap(item)

        if err != nil {
            fmt.Println("Got error marshalling map:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        // Create item in table Movies
        input := &dynamodb.PutItemInput{
            Item: av,
            TableName: aws.String("Movies"),
        }

        _, err = svc.PutItem(input)

        if err != nil {
            fmt.Println("Got error calling PutItem:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        fmt.Println("Successfully added '",item.Title,"' (",item.Year,") to Movies table")
    }
}

