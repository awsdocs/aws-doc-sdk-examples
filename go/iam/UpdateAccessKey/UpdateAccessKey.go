// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.update_access_key]
package main

// snippet-start:[iam.go.update_access_key.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.update_access_key.imports]

// ActivateAccessKey sets the status of an access key to active
// Inputs:
//     svs is an IAM service client
//     keyID is the ID of the access key
//     userName is the name of the user
// Output:
//     If success, nil
//     Otherwise, an error from the call to UpdateAccessKey
func ActivateAccessKey(svc iamiface.IAMAPI, keyID, userName *string) error {
    // snippet-start:[iam.go.update_access_key.call]
    _, err := svc.UpdateAccessKey(&iam.UpdateAccessKeyInput{
        AccessKeyId: keyID,
        Status:      aws.String(iam.StatusTypeActive),
        UserName:    userName,
    })
    // snippet-end:[iam.go.update_access_key.call]

    return err
}

func main() {
    // snippet-start:[iam.go.update_access_key.args]
    keyID := flag.String("k", "", "The ID of the access key")
    userName := flag.String("u", "", "The name of the user")
    flag.Parse()

    if *keyID == "" || *userName == "" {
        fmt.Println("You must supply an access key ID and user name (-k KEY-ID -u USER-NAME)")
        return
    }
    // snippet-end:[iam.go.update_access_key.args]

    // snippet-start:[iam.go.update_access_key.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.update_access_key.session]

    err := ActivateAccessKey(svc, keyID, userName)
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Access Key activated")
}
// snippet-end:[iam.go.update_access_key]
