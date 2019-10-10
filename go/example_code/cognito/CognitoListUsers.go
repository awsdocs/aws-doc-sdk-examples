// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[CognitoListUsers lists the users in a Amazon Cognito user pool.]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[ListUsers function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[cognito]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-12]
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
// snippet-start:[cognito.go.list_users]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

    "flag"
    "fmt"
    "os"
)

func main() {
    userPoolIdptr := flag.String("p", "", "The ID of the user pool")

    flag.Parse()

    userPoolID := *userPoolIdptr

    if userPoolID == "" {
        fmt.Println("You must supply a user pool ID")
        fmt.Println("Usage: go run CreateUser.go -p USER-POOL-ID")
        os.Exit(1)
    }

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )
    if err != nil {
        fmt.Println("Got error creating session:", err)
        os.Exit(1)
    }

    // Create Cognito service client
    cognitoClient := cognitoidentityprovider.New(sess)

    results, err := cognitoClient.ListUsers(
        &cognitoidentityprovider.ListUsersInput{
            UserPoolId: userPoolIdptr})
    if err != nil {
        fmt.Println("Got error listing users")
        os.Exit(1)
    }

    // Show their names an email addresses
    for _, user := range results.Users {
        attributes := user.Attributes

        for _, a := range attributes {
            if *a.Name == "name" {
                fmt.Println("Name:  " + *a.Value)
            } else if *a.Name == "email" {
                fmt.Println("Email: " + *a.Value)
            }
        }

        fmt.Println("")
    }
}
// snippet-end:[cognito.go.list_users]
