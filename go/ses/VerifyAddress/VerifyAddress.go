// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ses.go.verify_address]
package main

// snippet-start:[ses.go.verify_address.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/service/ses/sesiface"
)
// snippet-end:[ses.go.verify_address.imports]

// SendVerification sends an email to verify the recipient's address
// Inputs:
//     svc is the Amazon SES service client
//     recipient is the email address for the To value
// Output:
//     If success, nil
//     Otherwise, an error from the call to VerifyEmailAddress
func SendVerification(svc sesiface.SESAPI, recipient *string) error {
    // snippet-start:[ses.go.verify_address.call]
    _, err := svc.VerifyEmailAddress(&ses.VerifyEmailAddressInput{
        EmailAddress: recipient,
    })
    // snippet-end:[ses.go.verify_address.call]

    return err
}

func main() {
    // snippet-start:[ses.go.verify_address.args]
    recipient := flag.String("r", "", "The email address of the recipient")
    flag.Parse()

    if *recipient == "" {
        fmt.Println("You must supply an email address for the recipient")
        fmt.Println("-r RECIPIENT")
        return
    }
    // snippet-end:[ses.go.verify_address.args]

    // snippet-start:[ses.go.verify_address.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ses.New(sess)
    // snippet-end:[ses.go.verify_address.session]

    err := SendVerification(svc, recipient)
    if err != nil {
        fmt.Println("Got an error sending a validation email to " + *recipient)
        fmt.Println(err)
        return
    }

    fmt.Println("Email address: " + *recipient + " verified")
}
// snippet-end:[ses.go.verify_address]
