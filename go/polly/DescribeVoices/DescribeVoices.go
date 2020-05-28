// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[polly.go.describe_voices]
package main

// snippet-start:[polly.go.describe_voices.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/polly"
    "github.com/aws/aws-sdk-go/service/polly/pollyiface"
)
// snippet-end:[polly.go.describe_voices.imports]

// GetVoices retrieves a list of the voices for US English.
// Inputs:
//     svc is a Polly service client
// Output:
//     If success, the list of voices and nil
//     Otherwise, nil and an error from the call to DescribeVoices
func GetVoices(svc pollyiface.PollyAPI) (*polly.DescribeVoicesOutput, error) {
    // snippet-start:[polly.go.describe_voices.call]
    resp, err := svc.DescribeVoices(&polly.DescribeVoicesInput{
        LanguageCode: aws.String("en-US"),
    })
    // snippet-end:[polly.go.describe_voices.call]
    return resp, err
}

func main() {
    // snippet-start:[polly.go.describe_voices.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := polly.New(sess)
    // snippet-end:[polly.go.describe_voices.session]
    
    resp, err := GetVoices(svc)
    if err != nil {
        fmt.Println("Got error calling DescribeVoices:")
        fmt.Print(err.Error())
        return
    }

    // snippet-start:[polly.go.describe_voices.display]
    for _, v := range resp.Voices {
        fmt.Println("Name:   " + *v.Name)
        fmt.Println("Gender: " + *v.Gender)
        fmt.Println("")
    }
    // snippet-end:[polly.go.describe_voices.display]
}
// snippet-end:[polly.go.describe_voices]
