//snippet-sourceauthor: [Doug-AWS]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

package main

import (
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
)

func addNotification(functionName *string, sourceArn *string) {
    // Initialize a session
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Lambda service client
    svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    permArgs := &lambda.AddPermissionInput{
        Action:       aws.String("lambda:InvokeFunction"),
        FunctionName: functionName,
        Principal:    aws.String("s3.amazonaws.com"),
        SourceArn:    sourceArn,
        StatementId:  aws.String("lambda_s3_notification"),
    }

    result, err := svc.AddPermission(permArgs)

    if err != nil {
        fmt.Println("Cannot configure function for notifications")
        os.Exit(0)
    } else {
        fmt.Println(result)
    }
}

func main() {
    functionName := flag.String("f", "", "The name of the Lambda function")
    sourceArn := flag.String("a", "", "The ARN of the entity invoking the function")

    addNotification(functionName, sourceArn)
}
