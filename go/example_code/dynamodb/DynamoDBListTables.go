// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[DynamoDBListTables.go lists your Amazon DynamoDB tables.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[ListTables function]
// snippet-keyword:[Go]
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
// snippet-start:[dynamodb.go.list_tables]
package main

// snippet-start:[dynamodb.go.list_tables.imports]
import (
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"

    "fmt"
    "os"
)
// snippet-end:[dynamodb.go.list_tables.imports]

func main() {
    // snippet-start:[dynamodb.go.list_tables.session]
    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and region from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create DynamoDB client
    svc := dynamodb.New(sess)
    // snippet-end:[dynamodb.go.list_tables.session]

    // snippet-start:[dynamodb.go.list_tables.call]
    // Get the list of tables
    result, err := svc.ListTables(&dynamodb.ListTablesInput{})
    if err != nil {
        fmt.Println(err)
        os.Exit(1)
    }

    fmt.Println("Tables:")
    fmt.Println("")

    for _, n := range result.TableNames {
        fmt.Println(*n)
    }

    fmt.Println("")
    // snippet-end:[dynamodb.go.list_tables.call]
}
// snippet-end:[dynamodb.go.list_tables]