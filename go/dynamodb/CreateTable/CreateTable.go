// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0
// snippet-start:[dynamodb.go.create_new_table]
package main

// snippet-start:[dynamodb.go.create_new_table.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
)
// snippet-end:[dynamodb.go.create_new_table.imports]

// MakeTable creates an Amazon DynamoDB table
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     attributeDefinitions describe the table's attributes
//     keySchema defines the table schema
//     provisionedThroughput defines the throughput
//     tableName is the name of the table
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateTable
func MakeTable(svc dynamodbiface.DynamoDBAPI, attributeDefinitions []*dynamodb.AttributeDefinition, keySchema []*dynamodb.KeySchemaElement, provisionedThroughput *dynamodb.ProvisionedThroughput, tableName *string) error {
    // snippet-start:[dynamodb.go.create_new_table.call]

    _, err := svc.CreateTable(&dynamodb.CreateTableInput{
        AttributeDefinitions:  attributeDefinitions,
        KeySchema:             keySchema,
        ProvisionedThroughput: provisionedThroughput,
        TableName:             tableName,
    })
    // snippet-end:[dynamodb.go.create_new_table.call]
    return err
}

func main() {
    // snippet-start:[dynamodb.go.create_new_table.args]
    tableName := flag.String("t", "", "The name of the table")
    flag.Parse()

    if *tableName == "" {
        fmt.Println("You must supply a table name (-t TABLE)")
        return
    }
    // snippet-end:[dynamodb.go.create_new_table.args]

    // snippet-start:[dynamodb.go.create_new_table.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.create_new_table.session]

    // snippet-start:[dynamodb.go.create_new_table.create_input]
    attributeDefinitions := []*dynamodb.AttributeDefinition{
        {
            AttributeName: aws.String("Year"),
            AttributeType: aws.String("N"),
        },
        {
            AttributeName: aws.String("Title"),
            AttributeType: aws.String("S"),
        },
    }

    keySchema := []*dynamodb.KeySchemaElement{
        {
            AttributeName: aws.String("Year"),
            KeyType:       aws.String("HASH"),
        },
        {
            AttributeName: aws.String("Title"),
            KeyType:       aws.String("RANGE"),
        },
    }

    provisionedThroughput := &dynamodb.ProvisionedThroughput{
        ReadCapacityUnits:  aws.Int64(10),
        WriteCapacityUnits: aws.Int64(10),
    }
    // snippet-end:[dynamodb.go.create_new_table.create_input]

    err := MakeTable(svc, attributeDefinitions, keySchema, provisionedThroughput, tableName)
    if err != nil {
        fmt.Println("Got error calling CreateTable:")
        fmt.Println(err)
        return
    }

    fmt.Println("Created the table", *tableName)
}
// snippet-end:[dynamodb.go.create_new_table]
