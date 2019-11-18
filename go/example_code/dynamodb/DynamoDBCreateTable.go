// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[DynamoDBCreateTable.go create an Amazon DynamoDB table.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[CreateTable function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[dynamodb]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-18]
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
// snippet-start:[dynamodb.go.create_table]
package main

// snippet-start:[dynamodb.go.create_table.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"

    "fmt"
    "os"
)
// snippet-end:[dynamodb.go.create_table.imports]

func main() {
    // snippet-start:[dynamodb.go.create_table.session]
    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and region from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.create_table.session]

    // snippet-start:[dynamodb.go.create_table.call]
    // Create table Movies
    tableName := "Movies"

    input := &dynamodb.CreateTableInput{
        AttributeDefinitions: []*dynamodb.AttributeDefinition{
            {
                AttributeName: aws.String("Year"),
                AttributeType: aws.String("N"),
            },
            {
                AttributeName: aws.String("Title"),
                AttributeType: aws.String("S"),
            },
        },
        KeySchema: []*dynamodb.KeySchemaElement{
            {
                AttributeName: aws.String("Year"),
                KeyType:       aws.String("HASH"),
            },
            {
                AttributeName: aws.String("Title"),
                KeyType:       aws.String("RANGE"),
            },
        },
        ProvisionedThroughput: &dynamodb.ProvisionedThroughput{
            ReadCapacityUnits:  aws.Int64(10),
            WriteCapacityUnits: aws.Int64(10),
        },
        TableName: aws.String(tableName),
    }

    _, err := svc.CreateTable(input)
    if err != nil {
        fmt.Println("Got error calling CreateTable:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Created the table", tableName)
    // snippet-end:[dynamodb.go.create_table.call]
}
// snippet-end:[dynamodb.go.create_table]
