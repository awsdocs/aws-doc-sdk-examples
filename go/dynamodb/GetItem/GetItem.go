// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.get_item]
package main

// snippet-start:[dynamodb.go.get_item.imports]
import (
    "errors"
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)
// snippet-end:[dynamodb.go.get_item.imports]

// Item defines the item for the table
// snippet-start:[dynamodb.go.get_item.struct]
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
// snippet-end:[dynamodb.go.get_item.struct]

// GetTableItem retrieves the item with the year and title from the table
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     table is the name of the table
//     title is the movie title
//     year is when the movie was released
// Output:
//     If success, the information about the table item and nil
//     Otherwise, nil and an error from the call to GetItem or UnmarshalMap
func GetTableItem(svc dynamodbiface.DynamoDBAPI, table, title *string, year *int) (*Item, error) {
    // snippet-start:[dynamodb.go.get_item.call]
    result, err := svc.GetItem(&dynamodb.GetItemInput{
        TableName: table,
        Key: map[string]*dynamodb.AttributeValue{
            "year": {
                N: aws.String(strconv.Itoa(*year)),
            },
            "title": {
                S: title,
            },
        },
    })
    // snippet-end:[dynamodb.go.get_item.call]
    if err != nil {
        return nil, err
    }

    // snippet-start:[dynamodb.go.get_item.unmarshall]
    if result.Item == nil {
        msg := "Could not find '" + *title + "'"
        return nil, errors.New(msg)
    }

    item := Item{}

    err = dynamodbattribute.UnmarshalMap(result.Item, &item)
    // snippet-end:[dynamodb.go.get_item.unmarshall]
    if err != nil {
        return nil, err
    }

    return &item, nil
}

func main() {
    // snippet-start:[dynamodb.go.get_item.args]
    table := flag.String("t", "", "The table to retrieve item from")
    title := flag.String("n", "", "The name of the movie")
    year := flag.Int("y", -1, "The year the movie was released")
    flag.Parse()

    if *table == "" || *title == "" || *year == -1 {
        fmt.Println("You must supply a table name, movie title, and valid year")
        fmt.Println("(-t TABLE -n NAME -y YEAR")
        return
    }
    // snippet-end:[dynamodb.go.get_item.args]

    // snippet-start:[dynamodb.go.get_item.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.get_item.session]

    item, err := GetTableItem(svc, table, title, year)
    if err != nil {
        fmt.Println("Got an error retrieving the item:")
        fmt.Println(err)
        return
    }

    if item == nil {
        fmt.Println("Could not find the table entry")
        return
    }

    fmt.Println("Found item:")
    fmt.Println("Year:  ", item.Year)
    fmt.Println("Title: ", item.Title)
    fmt.Println("Plot:  ", item.Plot)
    fmt.Println("Rating:", item.Rating)
}
// snippet-end:[dynamodb.go.get_item]
