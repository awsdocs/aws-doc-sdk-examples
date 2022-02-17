package main

import (
  "os"
  "log"
  "context"
  "encoding/json"
  runtime "github.com/aws/aws-lambda-go/lambda"
  "github.com/aws/aws-lambda-go/events"
  "github.com/aws/aws-lambda-go/lambdacontext"
  "github.com/aws/aws-sdk-go/aws/session"
  "github.com/aws/aws-sdk-go/service/lambda"
)

var client = lambda.New(session.New())

func init() {
  callLambda()
}

func callLambda() (string, error) {
  input := &lambda.GetAccountSettingsInput{}
  req, resp := client.GetAccountSettingsRequest(input)
  err := req.Send()
  output, _ := json.Marshal(resp.AccountUsage)
  return string(output), err
}

func handleRequest(ctx context.Context, event events.SQSEvent) (string, error) {
  // event
  eventJson, _ := json.MarshalIndent(event, "", "  ")
  log.Printf("EVENT: %s", eventJson)
  // environment variables
  log.Printf("REGION: %s", os.Getenv("AWS_REGION"))
  log.Println("ALL ENV VARS:")
  for _, element := range os.Environ() {
    log.Println(element)
  }
  // request context
  lc, _ := lambdacontext.FromContext(ctx)
  log.Printf("REQUEST ID: %s", lc.AwsRequestID)
  // global variable
  log.Printf("FUNCTION NAME: %s", lambdacontext.FunctionName)
  // context method
  deadline, _ := ctx.Deadline()
  log.Printf("DEADLINE: %s", deadline)
  // AWS SDK call
  usage, err := callLambda()
  if err != nil {
    return "ERROR", err
  }
  return usage, nil
}

func main() {
  runtime.Start(handleRequest)
}