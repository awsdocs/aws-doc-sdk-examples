/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    "github.com/aws/aws-sdk-go/service/dynamodb/expression"
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

// Get the movies with a minimum rating of 8.0 in 2011
func main() {
    min_rating := 8.0
    year := 2011

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    if err != nil {
        fmt.Println("Got error creating session:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Create DynamoDB client
    svc := dynamodb.New(sess)

    // Create the Expression to fill the input struct with.
    // Get all movies in that year; we'll pull out those with a higher rating later
    filt := expression.Name("year").Equal(expression.Value(year))

    // Or we could get by ratings and pull out those with the right year later
    //    filt := expression.Name("info.rating").GreaterThan(expression.Value(min_rating))

    // Get back the title, year, and rating
    proj := expression.NamesList(expression.Name("title"), expression.Name("year"), expression.Name("info.rating"))

    expr, err := expression.NewBuilder().WithFilter(filt).WithProjection(proj).Build()

    if err != nil {
        fmt.Println("Got error building expression:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Build the query input parameters
    params := &dynamodb.ScanInput{
        ExpressionAttributeNames:  expr.Names(),
        ExpressionAttributeValues: expr.Values(),
        FilterExpression:          expr.Filter(),
        ProjectionExpression:      expr.Projection(),
        TableName:                 aws.String("Movies"),
    }

    // Make the DynamoDB Query API call
    result, err := svc.Scan(params)

    if err != nil {
        fmt.Println("Query API call failed:")
        fmt.Println((err.Error()))
        os.Exit(1)
    }

    num_items := 0

    for _, i := range result.Items {
        item := Item{}

        err = dynamodbattribute.UnmarshalMap(i, &item)

        if err != nil {
            fmt.Println("Got error unmarshalling:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        // Which ones had a higher rating?
        if item.Info.Rating > min_rating {
            // Or it we had filtered by rating previously:
            //   if item.Year == year {
            num_items += 1

            fmt.Println("Title: ", item.Title)
            fmt.Println("Rating:", item.Info.Rating)
            fmt.Println()
        }
    }

    fmt.Println("Found", num_items, "movie(s) with a rating above", min_rating, "in", year)
}
