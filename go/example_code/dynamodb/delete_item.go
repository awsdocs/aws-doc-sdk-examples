// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Deletes an item from an Amazon DynamoDB table.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[DeleteItem function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[dynamodb]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-12]
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
// snippet-start:[dynamodb.go.deleteitem]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"
    "github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
    
    "fmt"
    "os"
)

// Item has the values for a movie
type Item struct {
    Year  int    `json:"year"`
    Title string `json:"title"`
}

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create DynamoDB client
    svc := dynamodb.New(sess)

    item := Item{
        Year:  2015,
        Title: "The Big New Movie",
    }

    av, err := dynamodbattribute.MarshalMap(item)
    if err != nil {
        fmt.Println("Got error marshalling map:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    input := &dynamodb.DeleteItemInput{
        Key:       av,
        TableName: aws.String("Movies"),
    }

    _, err = svc.DeleteItem(input)
    if err != nil {
        fmt.Println("Got error calling DeleteItem")
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Deleted 'The Big New Movie' (2015)")
}
// snippet-end:[dynamodb.go.deleteitem]