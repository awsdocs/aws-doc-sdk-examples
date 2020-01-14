// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Lists the WorkDocs documents for a user.]
// snippet-keyword:[Amazon WorkDocs]
// snippet-keyword:[DescribeFolderContents function]
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
// snippet-start:[workdocs.go.list_user_docs.complete]
package main

// snippet-start:[workdocs.go.list_user_docs.imports]
import (
    "os"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/workdocs"

    "flag"
    "fmt"
)

// snippet-end:[workdocs.go.list_user_docs.imports]

func main() {
    // snippet-start:[workdocs.go.list_user_docs.vars]
    userPtr := flag.String("u", "", "User for whom info is retrieved")
    orgPtr := flag.String("o", "", "Your organization ID")

    flag.Parse()

    if *userPtr == "" || *orgPtr == "" {
        flag.PrintDefaults()
        os.Exit(1)
    }
    // snippet-end:[workdocs.go.list_user_docs.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    // snippet-start:[workdocs.go.list_user_docs.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := workdocs.New(sess)
    // snippet-end:[workdocs.go.list_user_docs.session]

    // snippet-start:[workdocs.go.list_user_docs.root_folder]
    input := new(workdocs.DescribeUsersInput)
    input.OrganizationId = orgPtr
    input.Query = userPtr

    result, err := svc.DescribeUsers(input)
    if err != nil {
        fmt.Println("Error getting user info", err)
        return
    }

    var folderID = ""

    if *result.TotalNumberOfUsers == 1 {
        for _, user := range result.Users {
            folderID = *user.RootFolderId
        }
                // snippet-end:[workdocs.go.list_user_docs.root_folder]

        // snippet-start:[workdocs.go.list_user_docs.describe]
        result, err := svc.DescribeFolderContents(&workdocs.DescribeFolderContentsInput{FolderId: &folderID})

        if err != nil {
            fmt.Println("Error getting docs for user", err)
            return
        }

        fmt.Println(*userPtr + " docs:")
        fmt.Println("")

        for _, doc := range result.Documents {
            fmt.Println(*doc.LatestVersionMetadata.Name)
            fmt.Println("  Size:         ", *doc.LatestVersionMetadata.Size, "(bytes)")
            fmt.Println("  Last modified:", *doc.LatestVersionMetadata.ModifiedTimestamp)
            fmt.Println("")
        }
        // snippet-end:[workdocs.go.list_user_docs.describe]
    }
}
// snippet-end:[workdocs.go.list_user_docs.complete]
