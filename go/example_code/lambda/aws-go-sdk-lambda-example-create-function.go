//snippet-sourceauthor: [Doug-AWS]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

package main

import (
    "flag"
    "fmt"
    "io/ioutil"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/lambda"
)

func createFunction(zipFileName string, bucketName string, functionName string, handler string, resourceArn string, runtime string) {
    // Initialize a session
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Lambda service client
    svc := lambda.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    contents, err := ioutil.ReadFile(zipFileName + ".zip")

    if err != nil {
        fmt.Println("Could not read " + zipFileName + ".zip")
        os.Exit(0)
    }

    createCode := &lambda.FunctionCode{
        S3Bucket:        aws.String(bucketName),
        S3Key:           aws.String(zipFileName),
        S3ObjectVersion: aws.String(""),
        ZipFile:         contents,
    }

    createArgs := &lambda.CreateFunctionInput{
        Code:         createCode,
        FunctionName: aws.String(functionName),
        Handler:      aws.String(handler),
        Role:         aws.String(resourceArn),
        Runtime:      aws.String(runtime),
    }

    result, err := svc.CreateFunction(createArgs)

    if err != nil {
        fmt.Println("Cannot create function: " + err.Error())
    } else {
        fmt.Println(result)
    }
}

func main() {
    zipFilePtr := flag.String("z", "", "The name of the ZIP file (without the .zip extension)")
    bucketPtr := flag.String("b", "", "the name of bucket to which the ZIP file is uploaded")
    functionPtr := flag.String("f", "", "The name of the Lambda function")
    handlerPtr := flag.String("h", "", "The name of the package.class handling the call")
    resourcePtr := flag.String("a", "", "The ARN of the role that calls the function")
    runtimePtr := flag.String("r", "", "The runtime for the function.")

    flag.Parse()

    zipFile := *zipFilePtr
    bucketName := *bucketPtr
    functionName := *functionPtr
    handler := *handlerPtr
    resourceArn := *resourcePtr
    runtime := *runtimePtr

    if zipFile == "" || bucketName == "" || functionName == "" || handler == "" || resourceArn == "" || runtime == "" {
        fmt.Println("You must supply a zip file name, bucket name, function name, handler, ARN, and runtime.")
        os.Exit(0)
    }

    createFunction(zipFile, bucketName, functionName, handler, resourceArn, runtime)
}
