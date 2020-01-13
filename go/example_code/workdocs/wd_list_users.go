// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Lists the WorkDocs users.]
// snippet-keyword:[Amazon WorkDocs]
// snippet-keyword:[DescribeUsers function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[workdocs]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-1-6]
/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[workdocs.go.list_users.complete]
package main

// snippet-start:[workdocs.go.list_users.imports]
import (
    "os"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/workdocs"

    "flag"
    "fmt"
)
// snippet-end:[workdocs.go.list_users.imports]

/*
  Lists all user names or extra info about user USER_NAME

  Usage:
    go run wd_list_users.go [USER_NAME]
*/

func main() {
    // snippet-start:[workdocs.go.list_users.vars]
    orgPtr := flag.String("o", "", "The ID of your organization")
    userPtr := flag.String("u", "", "User for whom info is retrieved")

    flag.Parse()

    if *orgPtr == "" {
        fmt.Println("You must supply the organization ID")
        flag.PrintDefaults()
        os.Exit(1)
    }
    // snippet-end:[workdocs.go.list_users.vars]

    // snippet-start:[workdocs.go.list_users.input]
    input := new(workdocs.DescribeUsersInput)
    input.OrganizationId = orgPtr

    // Show all users if we don't get a user name
    if *userPtr == "" {
        fmt.Println("Getting info about all users")
    } else {
        fmt.Println("Getting info about user " + *userPtr)
        input.Query = userPtr
    }
    // snippet-end:[workdocs.go.list_users.input]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    // snippet-start:[workdocs.go.list_users.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := workdocs.New(sess)
    // snippet-end:[workdocs.go.list_users.session]

    fmt.Println("")

    // snippet-start:[workdocs.go.list_users.describe]
    result, err := svc.DescribeUsers(input)
    if err != nil {
        fmt.Println("Error getting user info", err)
        return
    }

    if *userPtr == "" {
        fmt.Println("Found", *result.TotalNumberOfUsers, "users")
        fmt.Println("")
    }

    for _, user := range result.Users {
        fmt.Println("Username:   " + *user.Username)

        if *userPtr != "" {
            fmt.Println("Firstname:  " + *user.GivenName)
            fmt.Println("Lastname:   " + *user.Surname)
            fmt.Println("Email:      " + *user.EmailAddress)
            fmt.Println("Root folder " + *user.RootFolderId)
        }

        fmt.Println("")
    }
    // snippet-end:[workdocs.go.list_users.describe]
}
// snippet-end:[workdocs.go.list_users.complete]
