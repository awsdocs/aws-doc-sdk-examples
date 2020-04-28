// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.create_new_item]
package main

// snippet-start:[dynamodb.go.create_new_item.imports]
import (
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)
// snippet-end:[dynamodb.go.create_new_item.imports]

// Item holds info about new item
// snippet-start:[dynamodb.go.create_new_item.struct]
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
// snippet-end:[dynamodb.go.create_new_item.struct]

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
func AddTableItem(svc dynamodbiface.DynamoDBAPI, year *int, table, title, plot *string, rating *float64) error {
    // snippet-start:[dynamodb.go.create_new_item.assign_struct]
    item := Item{
        Year:   *year,
        Title:  *title,
        Plot:   *plot,
        Rating: *rating,
    }

    av, err := dynamodbattribute.MarshalMap(item)
    // snippet-end:[dynamodb.go.create_new_item.assign_struct]
    if err != nil {
        return err
    }

    // snippet-start:[dynamodb.go.create_new_item.call]
    _, err = svc.PutItem(&dynamodb.PutItemInput{
        Item:      av,
        TableName: table,
    })
    // snippet-end:[dynamodb.go.create_new_item.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[dynamodb.go.create_new_item.args]
    table := flag.String("d", "", "The name of the database table")
    year := flag.Int("y", 0, "The year the movie debuted")
    title := flag.String("t", "", "The title of the movie")
    plot := flag.String("p", "", "The plot of the movie")
    rating := flag.Float64("r", -1.0, "The movie rating, from 0.0 to 10.0")
    flag.Parse()

    if *table == "" || *year == 0 || *title == "" || *plot == "" || *rating == -1.0 {
        fmt.Println("You must supply a database table name, year, title, plot and rating")
        fmt.Println("-d TABLE -y YEAR -t TITLE -r RATING")
        return
    }
    // snippet-end:[dynamodb.go.create_new_item.args]

    // snippet-start:[dynamodb.go.create_new_item.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.create_new_item.session]

    err := AddTableItem(svc, year, table, title, plot, rating)
    if err != nil {
        fmt.Println("Got an error adding item to table:")
        fmt.Println(err)
        return
    }

    fmt.Println("Successfully added '"+*title+"' ("+strconv.Itoa(*year)+") to table "+*table+" with rating", *rating)
}
// snippet-end:[dynamodb.go.create_new_item]
