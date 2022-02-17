// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.list_users]
package main

// snippet-start:[iam.go.list_users.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
)
// snippet-end:[iam.go.list_users.imports]

// GetUsers retrieves a list of your IAM users
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     maxItems is the maximum number of users to return
// Output:
//     If success, the list of IAM users and nil
//     Otherwise, nil an error from the call to ListUsers
func GetUsers(sess *session.Session, maxItems *int64) (*iam.ListUsersOutput, error) {
    // snippet-start:[iam.go.list_users.call]
    svc := iam.New(sess)

    result, err := svc.ListUsers(&iam.ListUsersInput{
        MaxItems: maxItems,
    })
    // snippet-end:[iam.go.list_users.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[iam.go.list_users.args]
    maxUsers := flag.Int64("m", 10, "The maximum number of users to return")
    flag.Parse()

    if *maxUsers < int64(0) {
        *maxUsers = int64(10)
    }
    // snippet-end:[iam.go.list_users.args]

    // snippet-start:[iam.go.list_users.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[iam.go.list_users.session]

    result, err := GetUsers(sess, maxUsers)
    if err != nil {
        fmt.Println("Got an error retrieving users:")
        fmt.Println(err)
        return
    }

    for _, user := range result.Users {
        fmt.Println(*user.UserName+" created on", *user.CreateDate)
    }
}
// snippet-end:[iam.go.list_users]
