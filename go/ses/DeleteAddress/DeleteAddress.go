// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ses.go.delete_address]
package main

// snippet-start:[ses.go.delete_address.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/service/ses/sesiface"
)
// snippet-end:[ses.go.delete_address.imports]

// RemoveAddress deletes an Amazon SES email address
// Inputs:
//     svc is an Amazon SES service client
//     address is the email address to remove
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteVerifiedEmailAddress
func RemoveAddress(svc sesiface.SESAPI, address *string) error {
    // snippet-start:[ses.go.delete_address.call]
    _, err := svc.DeleteVerifiedEmailAddress(&ses.DeleteVerifiedEmailAddressInput{
        EmailAddress: address,
    })
    // snippet-end:[ses.go.delete_address.call]

    return err
}

func main() {
    // snippet-start:[ses.go.delete_address.args]
    address := flag.String("a", "", "The email address for the To field")
    flag.Parse()

    if *address == "" {
        fmt.Println("You must specify an email address")
        fmt.Println("-a ADDRESS")
        return
    }
    // snippet-end:[ses.go.delete_address.args]

    // snippet-start:[ses.go.delete_address.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ses.New(sess)
    // snippet-end:[ses.go.delete_address.session]

    err := RemoveAddress(svc, address)
    if err != nil {
        fmt.Println("Got an error attempting to remove the email address: " + *address)
        fmt.Println(err)
        return
    }

    // Display success message
    fmt.Println("Removed the email address: " + *address)
}
// snippet-end:[ses.go.delete_address]
