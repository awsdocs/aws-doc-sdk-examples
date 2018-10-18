//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Lists the Polly voices.]
//snippet-keyword:[Amazon Polly]
//snippet-keyword:[DescribeVoices function]
//snippet-keyword:[Go]
//snippet-service:[polly]
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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/polly"

    "fmt"
    "os"
)

func main() {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Polly client
    svc := polly.New(sess)

    // Get voices for US English
    input := &polly.DescribeVoicesInput{LanguageCode: aws.String("en-US")}

    resp, err := svc.DescribeVoices(input)
    if err != nil {
        fmt.Println("Got error calling DescribeVoices:")
        fmt.Print(err.Error())
        os.Exit(1)
    }

    for _, v := range resp.Voices {
        fmt.Println("Name:   " + *v.Name)
        fmt.Println("Gender: " + *v.Gender)
        fmt.Println("")
    }
}
