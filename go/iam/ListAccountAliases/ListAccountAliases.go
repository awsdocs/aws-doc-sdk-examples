// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.list_account_aliases]
package main

// snippet-start:[iam.go.list_account_aliases.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.list_account_aliases.imports]

// GetAccountAliases retrieves the aliases for your account.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     maxItems is the maximum number of aliases to retrieve
// Output:
//     If success, the list of aliases and nil
//     Otherwise, nil and an error from the call to ListAccountAliases
func GetAccountAliases(svc iamiface.IAMAPI, maxItems *int64) (*iam.ListAccountAliasesOutput, error) {
    // snippet-start:[iam.go.list_account_aliases.call]
    result, err := svc.ListAccountAliases(&iam.ListAccountAliasesInput{
        MaxItems: maxItems,
    })
    // snippet-end:[iam.go.list_account_aliases.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[iam.go.list_account_aliases.args]
    maxItems := flag.Int64("m", 10, "Maximum number of aliases to list")
    flag.Parse()

    if *maxItems < int64(0) {
        *maxItems = int64(10)
    }
    // snippet-end:[iam.go.list_account_aliases.args]

    // snippet-start:[iam.go.list_account_aliases.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.list_account_aliases.session]

    result, err := GetAccountAliases(svc, maxItems)
    if err != nil {
        fmt.Println("Got an error retrieving account aliases")
        fmt.Println(err)
        return
    }

    // snippet-start:[iam.go.list_account_aliases.display]
    for i, alias := range result.AccountAliases {
        fmt.Printf("Alias %d: %s\n", i, *alias)
    }
    // snippet-end:[iam.go.list_account_aliases.display]
}
// snippet-end:[iam.go.list_account_aliases]
