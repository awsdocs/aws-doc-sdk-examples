// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Updates an item in an Amazon DynamoDB table.]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[UpdateItem function]
// snippet-keyword:[Go]
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

package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"

    "fmt"
)

// ItemInfo holds info to update
type ItemInfo struct {
    Rating float64 `json:"rating"`
}

// Item identifies the item in the table
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

   info := ItemInfo{
        Rating: 0.5,
    }

    item := Item{
        Year:  2015,
        Title: "The Big New Movie",
    }

    expr, err := dynamodbattribute.MarshalMap(info)
    if err != nil {
        fmt.Println("Got error marshalling info:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    key, err := dynamodbattribute.MarshalMap(item)
    if err != nil {
        fmt.Println("Got error marshalling item:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Update item in table Movies
    input := &dynamodb.UpdateItemInput{
        ExpressionAttributeValues: expr,
        TableName:                 aws.String("Movies"),
        Key:                       key,
        ReturnValues:              aws.String("UPDATED_NEW"),
        UpdateExpression:          aws.String("set info.rating = :r"),
    }

    _, err = svc.UpdateItem(input)
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Successfully updated 'The Big New Movie' (2015) rating to 0.5")
}
