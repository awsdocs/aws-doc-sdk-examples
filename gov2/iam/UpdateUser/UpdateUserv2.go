// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.UpdateUser]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMUpdateUserAPI defines the interface for the UpdateUser function.
// We use this interface to test the function using a mocked service.
type IAMUpdateUserAPI interface {
    UpdateUser(ctx context.Context,
        params *iam.UpdateUserInput,
        optFns ...func(*iam.Options)) (*iam.UpdateUserOutput, error)
}

// RenameUser changes the name for an AWS Identity and Access Management (IAM) user.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a UpdateUserOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to UpdateUser.
func RenameUser(c context.Context, api IAMUpdateUserAPI, input *iam.UpdateUserInput) (*iam.UpdateUserOutput, error) {
    return api.UpdateUser(c, input)
}

func main() {
    userName := flag.String("u", "", "The name of the user")
    newName := flag.String("n", "", "The new name of the user")
    flag.Parse()

    if *userName == "" || *newName == "" {
        fmt.Println("You must supply a user name and new name (-u USERNAME -n NEW-NAME)")
        return
    }

    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := iam.NewFromConfig(cfg)

    input := &iam.UpdateUserInput{
        UserName:    userName,
        NewUserName: newName,
    }

    _, err = RenameUser(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got an error updating user " + *userName)
    }

    fmt.Println("Updated user name from: " + *userName + " to: " + *newName)
}

// snippet-end:[iam.go-v2.UpdateUser]
