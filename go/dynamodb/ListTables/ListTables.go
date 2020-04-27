// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.list_tables]
package main

// snippet-start:[dynamodb.go.list_tables.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
)
// snippet-end:[dynamodb.go.list_tables.imports]

// GetTables retrieves a list of your Amazon DynamoDB tables
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     limit is the maximum numbed of tables to return
// Output:
//     If success, a list of the tables and nil
//     Otherwise, nil and an error from the call to ListTables
func GetTables(sess *session.Session, limit *int64) ([]*string, error) {
    // snippet-start:[dynamodb.go.list_tables.call]
    svc := dynamodb.New(sess)

    result, err := svc.ListTables(&dynamodb.ListTablesInput{
        Limit: limit,
    })
    // snippet-end:[dynamodb.go.list_tables.call]
    if err != nil {
        return nil, err
    }

    return result.TableNames, nil
}

func main() {
    // snippet-start:[dynamodb.go.list_tables.args]
    limit := flag.Int64("l", 100, "How many tables to return")
    flag.Parse()

    if *limit < int64(0) {
        *limit = int64(10)
    }
    // snippet-end:[dynamodb.go.list_tables.args]

    // snippet-start:[dynamodb.go.list_tables.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[dynamodb.go.list_tables.session]

    tables, err := GetTables(sess, limit)
    if err != nil {
        fmt.Println("Got an error retrieving table names:")
        fmt.Println(err)
        return
    }

    // Get up to limit tables
    for _, n := range tables {
        fmt.Println(*n)
    }
}
// snippet-end:[dynamodb.go.list_tables]
