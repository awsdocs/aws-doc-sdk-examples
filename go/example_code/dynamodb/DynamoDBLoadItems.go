// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[DynamoDBLoadItems.go adds items from a JSON file to an Amazon DynamoDB table.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[PutItem function]
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
// snippet-start:[dynamodb.go.load_items]
package main

// snippet-start:[dynamodb.go.load_items.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"

    "encoding/json"
    "fmt"
    "io/ioutil"
    "os"
    "strconv"
)
// snippet-end:[dynamodb.go.load_items.imports]

// snippet-start:[dynamodb.go.load_items.struct]
// Create struct to hold info about new item
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
// snippet-end:[dynamodb.go.load_items.struct]

// snippet-start:[dynamodb.go.load_items.func]
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
// snippet-end:[dynamodb.go.load_items.func]

func main() {
    // snippet-start:[dynamodb.go.load_items.session]
    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and region from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.load_items.session]

    // snippet-start:[dynamodb.go.load_items.call]
    // Get table items from movie_data.json
    items := getItems()

    // Add each item to Movies table:
    tableName := "Movies"

    for _, item := range items {
        av, err := dynamodbattribute.MarshalMap(item)
        if err != nil {
            fmt.Println("Got error marshalling map:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        // Create item in table Movies
        input := &dynamodb.PutItemInput{
            Item:      av,
            TableName: aws.String(tableName),
        }

        _, err = svc.PutItem(input)
        if err != nil {
            fmt.Println("Got error calling PutItem:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        year := strconv.Itoa(item.Year)

        fmt.Println("Successfully added '" + item.Title + "' (" + year + ") to table " + tableName)
        // snippet-start:[dynamodb.go.load_items.call]
    }
}
// snippet-end:[dynamodb.go.load_items]
