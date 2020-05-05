// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.delete_user]
package main

// snippet-start:[iam.go.delete_user.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.delete_user.imports]

// RemoveUser deletss an IAM user
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     userName is the name of the user
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteUser
func RemoveUser(svc iamiface.IAMAPI, userName *string) error {
    // snippet-start:[iam.go.delete_user.call]
    _, err := svc.DeleteUser(&iam.DeleteUserInput{
        UserName: userName,
    })
    // snippet-end:[iam.go.delete_user.call]
    return err
}

func main() {
    // snippet-start:[iam.go.delete_user.args]
    userName := flag.String("u", "", "The name of the user")
    flag.Parse()

    if *userName == "" {
        fmt.Println("You must supply a user name (-u USERNAME)")
        return
    }
    // snippet-end:[iam.go.delete_user.args]

    // snippet-start:[iam.go.delete_user.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.delete_user.session]

    err := RemoveUser(svc, userName)
    if err != nil {
        fmt.Println("Got an error deleting user " + *userName)
    }

    fmt.Println("Deleted user " + *userName)
}
// snippet-end:[iam.go.delete_user]
