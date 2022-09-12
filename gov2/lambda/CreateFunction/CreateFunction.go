package main

import (
	"context"
	"log"
	"os"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/lambda"
	"github.com/aws/aws-sdk-go-v2/service/lambda/types"
)

//This example creates a Lambda Function in your AWS Account using the Go V2 SDK. Note that it requies you
// to have a zip file of the actual code already created and uploaded to an S3 Bucket.
func main() {

	//Your unique AWS Account Id
	awsAccountId := os.Getenv("AWS_ACCOUNT_ID")

	//example role that can be attached to the function. Here, we are using a role for dynamodb
	exampleRole := "lambda-dynamodb-role"

	//a unique function name. Your code can decide this
	lambdaFunctionName := "Test_Function_Name_xcdff3453"

	handlerName := "index.handler"
	roleName := "arn:aws:iam::" + awsAccountId + ":role/" + exampleRole
	runTimeName := "nodejs14.x"

	result, err := createLambdaFunction(lambdaFunctionName, handlerName, roleName, runTimeName)

	if err != nil {
		log.Fatalf("Error in creating Lambda Function : %s" + err.Error())
	}

	//Print the full function ARN if success
	log.Println(*result.FunctionArn)
}

//This function creates/uploads the lambda function using parameters. It uses aws gov2 SDK to call the CreateFunction() method
// Make sure you have the following environment variables set:
// AWS_ACCOUNT_ID, AWS_REGION, AWS_ACCESS_KEY, AWS_SECRET_KEY
func createLambdaFunction(lambdaFunctionName string, handlerName string, roleName string, runTimeName string) (*lambda.CreateFunctionOutput, error) {

	//default context to be used for calling the various SDK functions
	ctx := context.TODO()

	cfg, err := config.LoadDefaultConfig(ctx,
		config.WithRegion(os.Getenv("AWS_REGION")), //e.g. us-east-1
		config.WithCredentialsProvider(credentials.StaticCredentialsProvider{
			Value: aws.Credentials{
				AccessKeyID: os.Getenv("AWS_ACCESS_KEY"), SecretAccessKey: os.Getenv("AWS_SECRET_KEY"),
				Source: "example hard coded credentials",
			},
		}),
	)

	if err != nil {
		return nil, err
	}

	log.Println("Success: loaded config")

	//initialize Lambda Client from config
	lambdaClient := lambda.NewFromConfig(cfg)

	log.Println("Success: created Lambda Client")

	//runTime needs to be set. E.g. nodejs14.x
	runTime := types.Runtime(runTimeName)

	//initialize FunctionInput that will be used to setup the Lambda Function. This is where we
	// provide the S3 Bucket details to get the code from which is in .zip format
	input := &lambda.CreateFunctionInput{
		Code: &types.FunctionCode{
			S3Bucket: aws.String(os.Getenv("AWS_S3_BUCKET")),
			S3Key:    aws.String(os.Getenv("AWS_S3_KEY")), //e.g. code.zip
		},
		FunctionName: aws.String(lambdaFunctionName),
		Handler:      aws.String(handlerName),
		Publish:      *aws.Bool(true),
		Role:         aws.String(roleName),
		Runtime:      runTime,      // e.g. nodejs14.x
		Timeout:      aws.Int32(5), //timeout in 5 seconds.
	}

	//Create the Lambda Function in AWS using the input
	createFunctionOutput, err := lambdaClient.CreateFunction(ctx, input)
	if err != nil {
		return nil, err
	}

	sourceArnString := "arn:aws:events:" + os.Getenv("AWS_REGION") + ":" + os.Getenv("AWS_ACCOUNT_ID") + ":rule/" + lambdaFunctionName

	//Initialize Permissions for the Lambda Function
	permissionsInput := &lambda.AddPermissionInput{
		Action:       aws.String("lambda:InvokeFunction"),
		FunctionName: aws.String(lambdaFunctionName),
		Principal:    aws.String("events.amazonaws.com"),
		SourceArn:    aws.String(sourceArnString),
		StatementId:  aws.String("TrustCWEToInvokeMyLambdaFunction"),
	}
	_, permissionError := lambdaClient.AddPermission(ctx, permissionsInput)

	if permissionError != nil {
		return nil, permissionError
	}

	//return the *lambda.CreateFunctionOutput
	return createFunctionOutput, nil

}
