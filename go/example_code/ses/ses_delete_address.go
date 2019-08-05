// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Deletes an SES email address.]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Amazon SES]
// snippet-keyword:[DeleteVerifiedEmailAddress function]
// snippet-keyword:[Go]
// snippet-service:[ses]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "os"
    
    //go get -u github.com/aws/aws-sdk-go
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
)

const (
    // Replace sender@example.com with your "From" address
    Sender = "sender@example.com"
    
    // Replace recipient@example.com with a "To" address
    Recipient = "recipient@example.com"
)

func main() {
    // Create a new session in the us-west-2 region
    // Replace us-west-2 with the AWS Region you're using for Amazon SES
    sess, err := session.NewSession(&aws.Config{
        Region:aws.String("us-west-2")},
    )

    if err != nil {
        fmt.Println("Got error creating SES session:")
        fmt.Println(err.Error())
        os.Exit(1)
    }
    
    // Create an SES session
    svc := ses.New(sess)

    // Remove email address
    _, delErr := svc.DeleteVerifiedEmailAddress(&ses.DeleteVerifiedEmailAddressInput{EmailAddress: aws.String(Recipient)})
    
    // Display error message if it occurs
    if delErr != nil {
        fmt.Println("Got error attempting to remove email address: " + Recipient)
        fmt.Println(delErr.Error())
      os.Exit(1)
    }

    // Display success message
    fmt.Println("Removed email address: " + Recipient)
}
