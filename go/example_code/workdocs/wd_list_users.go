// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Lists the WorkDocs users.]
// snippet-keyword:[Amazon WorkDocs]
// snippet-keyword:[DescribeUsers function]
// snippet-keyword:[Go]
// snippet-service:[workdocs]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
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
    "github.com/aws/aws-sdk-go/service/workdocs"

    "flag"
    "fmt"
)

/*
  Lists all user names or extra info about user USER_NAME

  Usage:
    go run wd_list_users.go [USER_NAME]
 */

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create a Workdocs service client.
    svc := workdocs.New(sess)

    input := new(workdocs.DescribeUsersInput)

    // Replace with your organization ID
    org_id := "d-123456789c"
    input.OrganizationId = &org_id

    user_ptr := flag.String("u", "", "User for whom info is retrieved")
    flag.Parse()

    // Show all users if we don't get a user name
    if *user_ptr == "" {
        fmt.Println("Getting info about all users")
    } else {
        fmt.Println("Getting info about user " + *user_ptr)
        input.Query = user_ptr
    }

    fmt.Println("")

    result, err := svc.DescribeUsers(input)

    if err != nil {
        fmt.Println("Error getting user info", err)
        return
    }

    if *user_ptr == "" {
        fmt.Println("Found", *result.TotalNumberOfUsers, "users")
        fmt.Println("")
    }

    for _, user := range result.Users {
        fmt.Println("Username:   " + *user.Username)

        if *user_ptr != "" {
            fmt.Println("Firstname:  " + *user.GivenName)
            fmt.Println("Lastname:   " + *user.Surname)
            fmt.Println("Email:      " + *user.EmailAddress)
            fmt.Println("Root folder " + *user.RootFolderId)
        }

        fmt.Println("")
    }
}
