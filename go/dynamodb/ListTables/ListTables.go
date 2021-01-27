// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.list_all_tables]
package main

// snippet-start:[dynamodb.go.list_all_tables.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
)
// snippet-end:[dynamodb.go.list_all_tables.imports]

// GetTables retrieves a list of your Amazon DynamoDB tables
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     limit is the maximum number of tables to return
// Output:
//     If success, a list of the tables and nil
//     Otherwise, nil and an error from the call to ListTables
func GetTables(sess *session.Session, limit *int64) ([]*string, error) {
    // snippet-start:[dynamodb.go.list_all_tables.call]
    svc := dynamodb.New(sess)

    result, err := svc.ListTables(&dynamodb.ListTablesInput{
        Limit: limit,
    })
    // snippet-end:[dynamodb.go.list_all_tables.call]
    if err != nil {
        return nil, err
    }

    return result.TableNames, nil
}

func main() {
    // snippet-start:[dynamodb.go.list_all_tables.args]
    limit := flag.Int64("l", 100, "How many tables to return")
    flag.Parse()

    if *limit < int64(0) {
        *limit = int64(10)
    }
    // snippet-end:[dynamodb.go.list_all_tables.args]

    // snippet-start:[dynamodb.go.list_all_tables.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[dynamodb.go.list_all_tables.session]

    // Connecting via hard-coded credentials
    // Hard-Coded Credentials in an Application (Not Recommended)
    // Do not embed credentials inside an application. 
    // Use this method only for testing purposes.
    // Make sure to import "github.com/aws/aws-sdk-go/aws/credentials"
    // [Endpoint:optional] Mention the endpoint when connecting to local DynamoDB
    /* 
	sess, err := session.NewSession(&aws.Config{
		Region:      aws.String("us-west-2"),
		Endpoint:    aws.String("http://localhost:8000"),
		Credentials: credentials.NewStaticCredentials("AKID", "SECRET_KEY", "TOKEN"),
	})

	if err != nil {
		fmt.Println("Got an error fetching configurations")
		fmt.Println(err)
		return
	}
    */

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
// snippet-end:[dynamodb.go.list_all_tables]
