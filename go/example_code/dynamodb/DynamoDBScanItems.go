// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[DynamoDBScanItems.go gets items from and Amazon DymanoDB table using the Expression Builder package.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Scan function]
// snippet-keyword:[Expression Builder]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
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
// snippet-start:[dynamodb.go.scan_items]
package main

// snippet-start:[dynamodb.go.scan_items.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/expression"

    "fmt"
    "os"
)
// snippet-end:[dynamodb.go.scan_items.imports]

// snippet-start:[dynamodb.go.scan_items.struct]
// Create struct to hold info about new item
type Item struct {
    Year   int
    Title  string
    Plot   string
    Rating float64
}
// snippet-end:[dynamodb.go.scan_items.struct]

// Get the movies with a minimum rating of 8.0 in 2011
func main() {
    // snippet-start:[dynamodb.go.scan_items.session]
    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and region from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.scan_items.session]

    // snippet-start:[dynamodb.go.scan_items.vars]
    tableName := "Movies"
    minRating := 4.0
    year := 2013
    // snippet-end:[dynamodb.go.scan_items.vars]

    // snippet-start:[dynamodb.go.scan_items.expr]
    // Create the Expression to fill the input struct with.
    // Get all movies in that year; we'll pull out those with a higher rating later
    filt := expression.Name("Year").Equal(expression.Value(year))

    // Or we could get by ratings and pull out those with the right year later
    //    filt := expression.Name("info.rating").GreaterThan(expression.Value(min_rating))

    // Get back the title, year, and rating
    proj := expression.NamesList(expression.Name("Title"), expression.Name("Year"), expression.Name("Rating"))

    expr, err := expression.NewBuilder().WithFilter(filt).WithProjection(proj).Build()
    if err != nil {
        fmt.Println("Got error building expression:")
        fmt.Println(err.Error())
        os.Exit(1)
    }
    // snippet-end:[dynamodb.go.scan_items.expr]

    // snippet-start:[dynamodb.go.scan_items.call]
    // Build the query input parameters
    params := &dynamodb.ScanInput{
        ExpressionAttributeNames:  expr.Names(),
        ExpressionAttributeValues: expr.Values(),
        FilterExpression:          expr.Filter(),
        ProjectionExpression:      expr.Projection(),
        TableName:                 aws.String(tableName),
    }

    // Make the DynamoDB Query API call
    result, err := svc.Scan(params)
    if err != nil {
        fmt.Println("Query API call failed:")
        fmt.Println((err.Error()))
        os.Exit(1)
    }
    // snippet-end:[dynamodb.go.scan_items.call]

    // snippet-start:[dynamodb.go.scan_items.process]
    numItems := 0

    for _, i := range result.Items {
        item := Item{}

        err = dynamodbattribute.UnmarshalMap(i, &item)

        if err != nil {
            fmt.Println("Got error unmarshalling:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        // Which ones had a higher rating than minimum?
        if item.Rating > minRating {
            // Or it we had filtered by rating previously:
            //   if item.Year == year {
            numItems++

            fmt.Println("Title: ", item.Title)
            fmt.Println("Rating:", item.Rating)
            fmt.Println()
        }
    }

    fmt.Println("Found", numItems, "movie(s) with a rating above", minRating, "in", year)
    // snippet-end:[dynamodb.go.scan_items.process]
}
// snippet-end:[dynamodb.go.scan_items]
