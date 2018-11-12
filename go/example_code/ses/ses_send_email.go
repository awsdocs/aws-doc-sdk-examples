//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Sends a message to an SES email address.]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-keyword:[Amazon SES]
//snippet-keyword:[SendEmail function]
//snippet-keyword:[Go]
//snippet-service:[ses]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
    "fmt"
    
    //go get -u github.com/aws/aws-sdk-go
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/aws/awserr"
)

const (
    // Replace sender@example.com with your "From" address. 
    // This address must be verified with Amazon SES.
    Sender = "sender@example.com"
    
    // Replace recipient@example.com with a "To" address. If your account 
    // is still in the sandbox, this address must be verified.
    Recipient = "recipient@example.com"

    // Specify a configuration set. To use a configuration
    // set, comment the next line and line 92.
    //ConfigurationSet = "ConfigSet"
    
    // The subject line for the email.
    Subject = "Amazon SES Test (AWS SDK for Go)"
    
    // The HTML body for the email.
    HtmlBody =  "<h1>Amazon SES Test Email (AWS SDK for Go)</h1><p>This email was sent with " +
                "<a href='https://aws.amazon.com/ses/'>Amazon SES</a> using the " +
                "<a href='https://aws.amazon.com/sdk-for-go/'>AWS SDK for Go</a>.</p>"
    
    //The email body for recipients with non-HTML email clients.
    TextBody = "This email was sent with Amazon SES using the AWS SDK for Go."
    
    // The character encoding for the email.
    CharSet = "UTF-8"
)

func main() {
    // Create a new session in the us-west-2 region.
    // Replace us-west-2 with the AWS Region you're using for Amazon SES.
    sess, err := session.NewSession(&aws.Config{
        Region:aws.String("us-west-2")},
    )
    
    // Create an SES session.
    svc := ses.New(sess)
    
    // Assemble the email.
    input := &ses.SendEmailInput{
        Destination: &ses.Destination{
            CcAddresses: []*string{
            },
            ToAddresses: []*string{
                aws.String(Recipient),
            },
        },
        Message: &ses.Message{
            Body: &ses.Body{
                Html: &ses.Content{
                    Charset: aws.String(CharSet),
                    Data:    aws.String(HtmlBody),
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
        Source: aws.String(Sender),
            // Uncomment to use a configuration set
            //ConfigurationSetName: aws.String(ConfigurationSet),
    }

    // Attempt to send the email.
    result, err := svc.SendEmail(input)
    
    // Display error messages if they occur.
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok {
            switch aerr.Code() {
            case ses.ErrCodeMessageRejected:
                fmt.Println(ses.ErrCodeMessageRejected, aerr.Error())
            case ses.ErrCodeMailFromDomainNotVerifiedException:
                fmt.Println(ses.ErrCodeMailFromDomainNotVerifiedException, aerr.Error())
            case ses.ErrCodeConfigurationSetDoesNotExistException:
                fmt.Println(ses.ErrCodeConfigurationSetDoesNotExistException, aerr.Error())
            default:
                fmt.Println(aerr.Error())
            }
        } else {
            // Print the error, cast err to awserr.Error to get the Code and
            // Message from an error.
            fmt.Println(err.Error())
        }
    
        return
    }
    
    fmt.Println("Email Sent to address: " + Recipient)
    fmt.Println(result)
}
