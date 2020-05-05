// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.access_key_last_used]
package main

// snippet-start:[iam.go.access_key_last_used.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.access_key_last_used.imports]

// WhenWasKeyUsed retrieves when an access key was last used, including the region and with which service
// Inputs:
//     svc is an IAM service client
//     keyID is the ID of an access key
// Output:
//     If success, when the access key was last used and nil
//     Otherwise, nil and an error from the call to GetAccessKeyLastUsed
func WhenWasKeyUsed(svc iamiface.IAMAPI, keyID *string) (*iam.GetAccessKeyLastUsedOutput, error) {
    // snippet-start:[iam.go.access_key_last_used.call]
    result, err := svc.GetAccessKeyLastUsed(&iam.GetAccessKeyLastUsedInput{
        AccessKeyId: keyID,
    })
    // snippet-end:[iam.go.access_key_last_used.call]

    return result, err
}

func main() {
    // snippet-start:[iam.go.access_key_last_used.args]
    keyID := flag.String("k", "", "The ID of the access key")
    flag.Parse()

    if *keyID == "" {
        fmt.Println("You must supply the ID of an access key (-k KEY-ID)")
        return
    }
    // snippet-end:[iam.go.access_key_last_used.args]

    // snippet-start:[iam.go.access_key_last_used.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.access_key_last_used.session]

    result, err := WhenWasKeyUsed(svc, keyID)
    if err != nil {
        fmt.Println("Got an error retrieving when access key was last used:")
        fmt.Println(err)
        return
    }

    fmt.Println("The key was last used:", *result.AccessKeyLastUsed)
}
// snippet-end:[iam.go.access_key_last_used]
