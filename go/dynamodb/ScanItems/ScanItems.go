// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.scan_table_items]
package main

// snippet-start:[dynamodb.go.scan_table_items.imports]
import (
    "flag"
    "fmt"
    "strconv"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/expression"
)
// snippet-end:[dynamodb.go.scan_table_items.imports]

// Item holds info about the new item
// snippet-start:[dynamodb.go.scan_table_items.struct]
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}

// snippet-end:[dynamodb.go.scan_table_items.struct]

// ScanTableItems retrieves the table items released in a specific year
// Inputs:
//     svc is a LambdaDB service client
//     table is the name of the table
//     year is when the movie was released
// Output:
//     If success, the results of the scan and nil
//     Otherwise, nil and an error from the call to Scan
func ScanTableItems(sess *session.Session, year *int, table *string, minRating *float64) (*[]Item, error) {
    // snippet-start:[dynamodb.go.scan_table_items.expr]
    // Create the expression to fill the input struct.
    // Get all movies in that year; we'll pull out those with a higher rating later
    filt := expression.Name("Year").Equal(expression.Value(year))

    // Or we could get the movies by ratings and pull out those with the right year later
    //    filt := expression.Name("info.rating").GreaterThan(expression.Value(min_rating))

    // Get back the title, year, and rating
    proj := expression.NamesList(expression.Name("Title"), expression.Name("Year"), expression.Name("Rating"))

    expr, err := expression.NewBuilder().WithFilter(filt).WithProjection(proj).Build()
    // snippet-end:[dynamodb.go.scan_table_items.expr]
    if err != nil {
        fmt.Println("Got error building expression:")
        fmt.Println(err)
        return nil, err
    }

    // snippet-start:[dynamodb.go.scan_table_items.call]
    svc := dynamodb.New(sess)

    params := &dynamodb.ScanInput{
        ExpressionAttributeNames:  expr.Names(),
        ExpressionAttributeValues: expr.Values(),
        FilterExpression:          expr.Filter(),
        ProjectionExpression:      expr.Projection(),
        TableName:                 table,
    }

    result, err := svc.Scan(params)
    // snippet-end:[dynamodb.go.scan_table_items.call]
    if err != nil {
        return nil, err
    }

    var items []Item

    for _, i := range result.Items {
        item := Item{}

        err = dynamodbattribute.UnmarshalMap(i, &item)
        if err != nil {
            fmt.Println("Got error unmarshalling:")
            fmt.Println(err)
            return nil, err
        }

        // Which ones had a higher rating than the minimum value?
        if item.Rating > *minRating {
            // Or it we had filtered by rating previously:
            //   if item.Year == year {
            items = append(items, item)
        }
    }

    return &items, nil
}

// Get the movies with a minimum rating of 8.0 in 2011
func main() {
    // snippet-start:[dynamodb.go.scan_table_items.args]
    tableName := flag.String("t", "", "The name of the table")
    minRating := flag.Float64("r", -1.0, "The minumum rating of the movies to retrieve")
    year := flag.Int("y", -1, "The year the movies to retrieve were released")
    flag.Parse()

    if *tableName == "" || *minRating < 0.0 || *year < 0 {
        fmt.Println("You must supply a table name, minimum rating of 0.0, and year > 0 but < 2020")
        fmt.Println("(-t TABLE -r RATING -y YEAR)")
        return
    }
    // snippet-end:[dynamodb.go.scan_table_items.args]

    // snippet-start:[dynamodb.go.scan_table_items.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[dynamodb.go.scan_table_items.session]

    result, err := ScanTableItems(sess, year, tableName, minRating)
    if err != nil {
        fmt.Println("Got an error scanning table:")
        fmt.Println(err)
        return
    }

    // snippet-start:[dynamodb.go.scan_table_items.process]

    fmt.Println("Found", strconv.Itoa(len(*result)), "movie(s) with a rating above", *minRating, "in", *year)
    // snippet-end:[dynamodb.go.scan_table_items.process]
}
// snippet-end:[dynamodb.go.scan_table_items]
