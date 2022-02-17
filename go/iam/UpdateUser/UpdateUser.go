// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.update_user]
package main

// snippet-start:[iam.go.update_user.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.update_user.imports]

// RenameUser changes the name for an IAM user.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     userName is the name of the user
//     newName is the new user name
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateUser
func RenameUser(svc iamiface.IAMAPI, userName, newName *string) error {
    // snippet-start:[iam.go.update_user.call]
    _, err := svc.UpdateUser(&iam.UpdateUserInput{
        UserName:    userName,
        NewUserName: newName,
    })
    // snippet-end:[iam.go.update_user.call]
    return err
}

func main() {
    // snippet-start:[iam.go.update_user.args]
    userName := flag.String("u", "", "The name of the user")
    newName := flag.String("n", "", "The new name of the user")
    flag.Parse()

    if *userName == "" || *newName == "" {
        fmt.Println("You must supply a user name and new name (-u USERNAME -n NEW-NAME)")
        return
    }
    // snippet-end:[iam.go.update_user.args]

    // snippet-start:[iam.go.update_user.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.update_user.session]

    err := RenameUser(svc, userName, newName)
    if err != nil {
        fmt.Println("Got an error updating user " + *userName)
    }

    fmt.Println("Updated user " + *userName)
}
// snippet-end:[iam.go.update_user]
