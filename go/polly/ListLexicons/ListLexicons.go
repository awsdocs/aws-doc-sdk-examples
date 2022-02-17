// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[polly.go.list_lexicons]
package main

// snippet-start:[polly.go.list_lexicons.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/polly"
    "github.com/aws/aws-sdk-go/service/polly/pollyiface"
)
// snippet-end:[polly.go.list_lexicons.imports]

// GetLexicons retrieves the list of Amazon Polly lexicons.
// Inputs:
//     svc is an Amazon Polly service client
// Output:
//     If success, the list of lexicons and nil
//     Otherwise, nil and an error from the call to ListLexicons
func GetLexicons(svc pollyiface.PollyAPI) (*polly.ListLexiconsOutput, error) {
    // snippet-start:[polly.go.list_lexicons.call]
    resp, err := svc.ListLexicons(nil)
    // snippet-end:[polly.go.list_lexicons.call]

    return resp, err
}

func main() {
    // snippet-start:[polly.go.list_lexicons.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := polly.New(sess)
    // snippet-end:[polly.go.list_lexicons.session]

    resp, err := GetLexicons(svc)
    if err != nil {
        fmt.Println("Got an error calling ListLexicons:")
        fmt.Print(err)
        return
    }

    // snippet-start:[polly.go.list_lexicons.display]
    if len(resp.Lexicons) == 0 {
        fmt.Println("Did not retrieve any lexicons")
        return
    }

    for _, l := range resp.Lexicons {
        fmt.Println(*l.Name)
        fmt.Println("  Alphabet: " + *l.Attributes.Alphabet)
        fmt.Println("  Language: " + *l.Attributes.LanguageCode)
        fmt.Println("")
    }
    // snippet-end:[polly.go.list_lexicons.display]
}
// snippet-end:[polly.go.list_lexicons]
