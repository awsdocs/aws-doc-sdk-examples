// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ses.go.send_message]
package main

// snippet-start:[ses.go.send_message.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/service/ses/sesiface"
)
// snippet-end:[ses.go.send_message.imports]

// snippet-start:[ses.go.send_message.consts]
const (
    // Subject is the subject line for the email
    Subject = "Amazon SES Test (AWS SDK for Go)"

    // HTMLBody is the HTML body for the email
    HTMLBody = "<h1>Amazon SES Test Email (AWS SDK for Go)</h1><p>This email was sent with " +
        "<a href='https://aws.amazon.com/ses/'>Amazon SES</a> using the " +
        "<a href='https://aws.amazon.com/sdk-for-go/'>AWS SDK for Go</a>.</p>"

    // TextBody is the email body for recipients with non-HTML email clients
    TextBody = "This email was sent with Amazon SES using the AWS SDK for Go."

    // CharSet is the character encoding for the email
    CharSet = "UTF-8"
)
// snippet-end:[ses.go.send_message.consts]

// SendMsg sends an email message to an Amazon SES recipient
// Inputs:
//     svc is the Amazon SES service client
//     sender is the email address in the From field
//     recipient is the email address in the To field
// Output:
//     If success, nil
//     Otherwise, an error from the call to SendEmail
func SendMsg(svc sesiface.SESAPI, sender, recipient *string) error {
    // snippet-start:[ses.go.send_message.call]
    _, err := svc.SendEmail(&ses.SendEmailInput{
        Destination: &ses.Destination{
            CcAddresses: []*string{},
            ToAddresses: []*string{
                recipient,
            },
        },
        Message: &ses.Message{
            Body: &ses.Body{
                Html: &ses.Content{
                    Charset: aws.String(CharSet),
                    Data:    aws.String(HTMLBody),
                },
                Text: &ses.Content{
                    Charset: aws.String(CharSet),
                    Data:    aws.String(TextBody),
                },
            },
            Subject: &ses.Content{
                Charset: aws.String(CharSet),
                Data:    aws.String(Subject),
            },
        },
        Source: sender,
    })
    // snippet-end:[ses.go.send_message.call]

    return err
}

func main() {
    // snippet-start:[ses.go.send_message.args]
    sender := flag.String("t", "", "The email address for the 'From' field")
    recipient := flag.String("t", "", "The email address for the 'To' field")
    subject := flag.String("s", "Amazon SES Test (AWS SDK for Go)", "The text for the 'Subject' field")
    flag.Parse()

    if *sender == "" || *recipient == "" || *subject == "" {
        fmt.Println("You must supply an email address for the sender and recipient, and a subject")
        fmt.Println("-f SENDER -t RECIPIENT -s SUBJECT")
        return
    }
    // snippet-end:[ses.go.send_message.args]

    // snippet-start:[ses.go.send_message.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ses.New(sess)
    // snippet-end:[ses.go.send_message.session]

    err := SendMsg(svc, sender, recipient)
    if err != nil {
        fmt.Println("Got an error sending message:")
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Email sent to address: " + *recipient)
}
// snippet-end:[ses.go.send_message]
