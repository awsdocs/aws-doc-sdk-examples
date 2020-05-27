// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[glacier.go.create_vault]
package main

// snippet-start:[glacier.go.create_vault.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/glacier"
    "github.com/aws/aws-sdk-go/service/glacier/glacieriface"
)
// snippet-end:[glacier.go.create_vault.imports]

// MakeVault creates an Amazon Simple Storage Service Glacier vault.
// Inputs:
//     svc is an Amazon S3 Glacier service client
//     name is the name of the vault
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateVault
func MakeVault(svc glacieriface.GlacierAPI, name *string) error {
    // snippet-start:[glacier.go.create_vault.call]
    _, err := svc.CreateVault(&glacier.CreateVaultInput{
        VaultName: name,
    })
    // snippet-end:[glacier.go.create_vault.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[glacier.go.create_vault.args]
    name := flag.String("v", "", "The name of the vault")
    flag.Parse()

    if *name == "" {
        fmt.Println("You must supply the name of the vault (-v VAULT-NAME)")
        return
    }
    // snippet-end:[glacier.go.create_vault.args]

    // snippet-start:[glacier.go.create_vault.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := glacier.New(sess)
    // snippet-end:[glacier.go.create_vault.session]

    err := MakeVault(svc, name)
    if err != nil {
        fmt.Println("Got an error creating the vault:")
        fmt.Println(err)
        return
    }

    fmt.Println("Created vault")
}
// snippet-end:[glacier.go.create_vault]
