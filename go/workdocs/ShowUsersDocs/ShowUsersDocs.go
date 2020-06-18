// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[workdocs.go.show_user_docs]
package main

// snippet-start:[workdocs.go.show_user_docs.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/workdocs"
    "github.com/aws/aws-sdk-go/service/workdocs/workdocsiface"
)
// snippet-end:[workdocs.go.show_user_docs.imports]

// ShowUserDocs lists the Amazon WorkDocs docs for a user.
// Inputs:
//     svc is an Amazon WorkDocs service client
//     orgID is the ID of the organization to which the user belongs
//     user is the name of the user
// Output:
//     If success, a list of documents and nil
//     Otherwise, nil and an error from the call to DescribeUsers or DescribeFolderContents
func ShowUserDocs(svc workdocsiface.WorkDocsAPI, orgID, user *string) (*workdocs.DescribeFolderContentsOutput, error) {
    // snippet-start:[workdocs.go.show_user_docs.describe]
    result, err := svc.DescribeUsers(&workdocs.DescribeUsersInput{
        OrganizationId: orgID,
        Query:          user,
    })
    // snippet-end:[workdocs.go.show_user_docs.describe]
    if err != nil {
        return nil, err
    }

    // snippet-start:[workdocs.go.show_user_docs.contents]
    for _, user := range result.Users {
        result, err := svc.DescribeFolderContents(&workdocs.DescribeFolderContentsInput{
            FolderId: user.RootFolderId,
        })
        // snippet-end:[workdocs.go.show_user_docs.contents]

        return result, err
    }

    return nil, nil
}

func main() {
    // snippet-start:[workdocs.go.show_user_docs.args]
    userName := flag.String("u", "", "The name of the user")
    orgID := flag.String("o", "", "Your organization ID")

    flag.Parse()

    if *userName == "" || *orgID == "" {
        fmt.Println("You must supply the ID of the organization and a user name")
        fmt.Println("-o ORG-ID -u USER-NAME")
        return
    }
    // snippet-end:[workdocs.go.show_user_docs.args]

    // snippet-start:[workdocs.go.show_user_docs.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := workdocs.New(sess)
    // snippet-end:[workdocs.go.show_user_docs.session]

    result, err := ShowUserDocs(svc, orgID, userName)
    if err != nil {
        fmt.Println("Got an error retrieving the docs:")
        fmt.Println(err)
        return
    }

    // snippet-start:[workdocs.go.show_user_docs.display]
    fmt.Println(*userName + " docs:")
    fmt.Println("")

    for _, doc := range result.Documents {
        fmt.Println(*doc.LatestVersionMetadata.Name)
        fmt.Println("  Size:         ", *doc.LatestVersionMetadata.Size, "(bytes)")
        fmt.Println("  Last modified:", *doc.LatestVersionMetadata.ModifiedTimestamp)
        fmt.Println("")
    }
    // snippet-end:[workdocs.go.show_user_docs.display]
}
// snippet-end:[workdocs.go.show_user_docs]
