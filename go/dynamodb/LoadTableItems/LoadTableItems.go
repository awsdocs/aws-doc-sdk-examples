// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.add_items]
package main

// snippet-start:[dynamodb.go.add_items.imports]
import (
    "encoding/json"
    "flag"
    "fmt"
    "io/ioutil"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)
// snippet-end:[dynamodb.go.add_items.imports]

// Item holds info about new item
// snippet-start:[dynamodb.go.add_items.struct]
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
// snippet-end:[dynamodb.go.add_items.struct]

// GetItems creates a list of table items from JSON file
func GetItems(filename *string) ([]Item, error) {
    raw, err := ioutil.ReadFile(*filename)
    if err != nil {
        return nil, err
    }

    var items []Item
    err = json.Unmarshal(raw, &items)
    if err != nil {
        return nil, err
    }

    return items, nil
}

// GetMovieName gets the name of the movie from an attribute value
func GetMovieName(av map[string]*dynamodb.AttributeValue) (string, error) {
    var item Item

    err := dynamodbattribute.UnmarshalMap(av, &item)
    if err != nil {
        return "", err
    }

    return item.Title, nil
}

// AddTableItem adds an item to an Amazon DynamoDB table
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     year is the year when the movie was released
//     table is the name of the table
//     title is the movie title
//     plot is a summary of the plot of the movie
//     rating is the movie rating, from 0.0 to 10.0
// Output:
//     If success, nil
//     Otherwise, an error from the call to PutItem
func AddTableItem(svc dynamodbiface.DynamoDBAPI, av map[string]*dynamodb.AttributeValue, table *string) error {
    // snippet-start:[dynamodb.go.add_items.call]
    _, err := svc.PutItem(&dynamodb.PutItemInput{
        Item:      av,
        TableName: table,
    })
    // snippet-end:[dynamodb.go.add_items.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[dynamodb.go.add_items.args]
    jsonFile := flag.String("j", "", "The JSON file containing the items to add to the table")
    table := flag.String("d", "", "The name of the database table")
    flag.Parse()

    if *jsonFile == "" || *table == "" {
        fmt.Println("You must supply a JSON filename and database table name")
        fmt.Println("-j JSON-FILE -d TABLE")
        return
    }
    // snippet-end:[dynamodb.go.add_items.args]

    // snippet-start:[dynamodb.go.add_items.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.add_items.session]

    // snippet-start:[dynamodb.go.add_items.get_items]
    items, err := GetItems(jsonFile)
    if err != nil {
        fmt.Println("Got an error retrieving items:")
        fmt.Println(err)
        return
    }

    for _, item := range items {
        av, err := dynamodbattribute.MarshalMap(item)
        if err != nil {
            fmt.Println("Got error marshalling map:")
            fmt.Println(err)
            return
        }

        err = AddTableItem(svc, av, table)
        if err != nil {
            fmt.Println("Got an error adding item to table:")
            fmt.Println(err)
            return
        }

        title, err := GetMovieName(av)
        if err != nil {
            fmt.Println("Got an error retrieving movie name:")
            fmt.Println(err)
            return
        }

        fmt.Println("Successfully added '" + title + " to table " + *table)
    }
    // snippet-end:[dynamodb.go.add_items.get_items]
}
// snippet-end:[dynamodb.go.add_items]
