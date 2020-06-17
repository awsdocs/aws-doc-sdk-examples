// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.list_access_keys]
package main

// snippet-start:[iam.go.list_access_keys.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.list_access_keys.imports]

// GetAccessKeys retrieves up to maxItems access keys for user userName
// Inputs:
//     svc is an IAM service client
//     maxItems is the maximum number of access keys to return
//     userName is the name of the user
// Output:
//     If success, a list of access keys and nil
//     Otherwise, nil and an error from the call to ListAccessKeys
func GetAccessKeys(svc iamiface.IAMAPI, maxItems *int64, userName *string) (*iam.ListAccessKeysOutput, error) {
    // snippet-start:[iam.go.list_access_keys.call]
    result, err := svc.ListAccessKeys(&iam.ListAccessKeysInput{
        MaxItems: maxItems,
        UserName: userName,
    })
    // snippet-end:[iam.go.list_access_keys.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[iam.go.list_access_keys.args]
    maxItems := flag.Int64("m", 10, "The maximum number of access keys to show")
    userName := flag.String("u", "", "The name of the user")
    flag.Parse()

    if *userName == "" {
        fmt.Println("You must supply the name of a user (-u USER)")
        return
    }

    if *maxItems < int64(0) {
        *maxItems = int64(10)
    }
    // snippet-end:[iam.go.list_access_keys.args]

    // snippet-start:[iam.go.list_access_keys.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.list_access_keys.session]

    result, err := GetAccessKeys(svc, maxItems, userName)
    if err != nil {
        fmt.Println("Got an error retrieving user access keys:")
        fmt.Println(err)
        return
    }

    for _, key := range result.AccessKeyMetadata {
        fmt.Println("Status for access key " + *key.AccessKeyId + ": " + *key.Status)
    }
}
// snippet-end:[iam.go.list_access_keys]
