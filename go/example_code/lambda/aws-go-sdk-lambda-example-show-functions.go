 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Go]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


package main

import (
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
)

// Lists all of your Lambda functions in us-west-2

func main() {
    // Initialize a session
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Lambda service client
    svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    result, err := svc.ListFunctions(nil)

    if err != nil {
        fmt.Println("Cannot list functions")
        os.Exit(0)
    }

    fmt.Println("Functions:")

    for _, f := range result.Functions {
        fmt.Println("Name:        " + aws.StringValue(f.FunctionName))
        fmt.Println("Description: " + aws.StringValue(f.Description))
        fmt.Println("")
    }
}
