// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.delete_table_item]
package main

// snippet-start:[dynamodb.go.delete_table_item.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)
// snippet-end:[dynamodb.go.delete_table_item.imports]

// DeleteTableItem deletes an item from a table
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     table is the name of the table
//     movie is the name of the movie
//     year is when the movie was released
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteItem
func DeleteTableItem(svc dynamodbiface.DynamoDBAPI, table, movie, year *string) error {
    // snippet-start:[dynamodb.go.delete_table_item.call]
    input := &dynamodb.DeleteItemInput{
        Key: map[string]*dynamodb.AttributeValue{
            "Year": {
                N: year,
            },
            "Title": {
                S: movie,
            },
        },
        TableName: table,
    }

    _, err := svc.DeleteItem(input)
    // snippet-end:[dynamodb.go.delete_table_item.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[dynamodb.go.delete_table_item.args]
    table := flag.String("t", "", "The name of the table")
    movie := flag.String("m", "", "The name of the movie")
    year := flag.String("y", "", "The year the movie was released")
    flag.Parse()

    if *table == "" || *movie == "" || *year == "" {
        fmt.Println("You must specify a table, movie title, and year the movie was released:")
        fmt.Println("-t TABLE -m MOVIE -y YEAR")
        return
    }
    // snippet-end:[dynamodb.go.delete_table_item.args]

    // snippet-start:[dynamodb.go.delete_table_item.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.delete_table_item.session]

    err := DeleteTableItem(svc, table, movie, year)
    if err != nil {
        fmt.Println("Got an error deleting movie " + *movie + " from table " + *table + ":")
        fmt.Println(err)
        return
    }

    fmt.Println("Deleted '" + *movie + " from table " + *table)
}
// snippet-end:[dynamodb.go.delete_table_item]
