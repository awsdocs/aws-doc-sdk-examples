// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[workdocs.go.show_users]
package main

// snippet-start:[workdocs.go.show_users.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/workdocs"
    "github.com/aws/aws-sdk-go/service/workdocs/workdocsiface"
)
// snippet-end:[workdocs.go.show_users.imports]

// ShowUsers lists the users for an organization
// Inputs:
//     svc is an Amazon WorkDocs service client
//     orgID is the ID of the organization
// Output:
//     If success, information about the users and nil
//     Otherwise, nil and an error from the call to DescribeUsers
func ShowUsers(svc workdocsiface.WorkDocsAPI, orgID *string) (*workdocs.DescribeUsersOutput, error) {
    // snippet-start:[workdocs.go.show_users.call]
    result, err := svc.DescribeUsers(&workdocs.DescribeUsersInput{
        OrganizationId: orgID,
    })
    // snippet-end:[workdocs.go.show_users.call]

    return result, err
}

func main() {
    // snippet-start:[workdocs.go.show_users.args]
    orgID := flag.String("o", "", "The ID of your organization")

    flag.Parse()

    if *orgID == "" {
        fmt.Println("You must supply the organization ID")
        fmt.Println("-o ORG-ID")
        return
    }
    // snippet-end:[workdocs.go.show_users.args]

    // snippet-start:[workdocs.go.show_users.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := workdocs.New(sess)
    // snippet-end:[workdocs.go.show_users.session]

    result, err := ShowUsers(svc, orgID)
    if err != nil {
        fmt.Println("Error getting user info", err)
        return
    }

    // snippet-start:[workdocs.go.show_users.display]
    fmt.Println("Found", *result.TotalNumberOfUsers, "user(s)")
    fmt.Println("")

    for _, user := range result.Users {
        fmt.Println("Username:   " + *user.Username)
        fmt.Println("Firstname:  " + *user.GivenName)
        fmt.Println("Lastname:   " + *user.Surname)
        fmt.Println("Email:      " + *user.EmailAddress)
        fmt.Println("Root folder " + *user.RootFolderId)

        fmt.Println("")
    }
    // snippet-end:[workdocs.go.show_users.display]
}
// snippet-end:[workdocs.go.show_users]
