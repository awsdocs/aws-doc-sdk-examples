package main

import (
	"os"
	"encoding/json"
	"time"
	"context"
	"testing"
	"strings"
	"io/ioutil"
	"github.com/aws/aws-lambda-go/lambdacontext"
	"github.com/aws/aws-lambda-go/events"
)

func TestMain(t *testing.T) {
	d := time.Now().Add(50 * time.Millisecond)
	os.Setenv("AWS_LAMBDA_FUNCTION_NAME","blank-go")
	ctx, _ := context.WithDeadline(context.Background(), d)
	ctx = lambdacontext.NewContext(ctx, &lambdacontext.LambdaContext{
		AwsRequestID:       "495b12a8-xmpl-4eca-8168-160484189f99",
		InvokedFunctionArn: "arn:aws:lambda:us-east-2:123456789012:function:blank-go",
	})
	inputJson := ReadJSONFromFile(t, "../event.json")
	var event events.SQSEvent
	err := json.Unmarshal(inputJson, &event)
	if err != nil {
		t.Errorf("could not unmarshal event. details: %v", err)
	}
	//var inputEvent SQSEvent
	result, err := handleRequest(ctx, event)
	if err != nil  {
	t.Log(err)
	}
	t.Log(result)
	if !strings.Contains(result, "FunctionCount") {
		t.Errorf("Output does not contain FunctionCode.")
	}
}
func ReadJSONFromFile(t *testing.T, inputFile string) []byte {
	inputJSON, err := ioutil.ReadFile(inputFile)
	if err != nil {
		t.Errorf("could not open test file. details: %v", err)
	}

	return inputJSON
}
