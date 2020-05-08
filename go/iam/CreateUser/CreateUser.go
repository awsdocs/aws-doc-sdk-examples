// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.create_user]
package main

// snippet-start:[iam.go.create_user.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.create_user.imports]

// MakeUser VERBs an Amazon/AWS SERVICE RESOURCE
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     userName is the name of the user
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateUser
func MakeUser(svc iamiface.IAMAPI, userName *string) error {
    // snippet-start:[iam.go.create_user.call]
    _, err := svc.CreateUser(&iam.CreateUserInput{
        UserName: userName,
    })
    // snippet-end:[iam.go.create_user.call]
    return err
}

func main() {
    // snippet-start:[iam.go.create_user.args]
    userName := flag.String("u", "", "The name of the user")
    flag.Parse()

    if *userName == "" {
        fmt.Println("You must supply a user name (-u USERNAME)")
        return
    }
    // snippet-end:[iam.go.create_user.args]

    // snippet-start:[iam.go.create_user.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.create_user.session]

    err := MakeUser(svc, userName)
    if err != nil {
        fmt.Println("Got an error creating user " + *userName)
    }

    fmt.Println("Created user " + *userName)
}
// snippet-end:[iam.go.create_user]
