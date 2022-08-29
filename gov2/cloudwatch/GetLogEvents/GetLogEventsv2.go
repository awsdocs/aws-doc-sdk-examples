package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs"
)

// CWLGetLogEventsAPI defines the interface for the GetLogEvents function
type CWLGetLogEventsAPI interface {
	GetLogEvents(ctx context.Context, params *cloudwatchlogs.GetLogEventsInput, optFns ...func(*cloudwatchlogs.Options)) (*cloudwatchlogs.GetLogEventsOutput, error)
}

// GetEvents retrieves cloudwatch log events from the specified log group and log stream
// Inputs:
//     ctx is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a GetLogEventsOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to GetLogEvents
func GetEvents(ctx context.Context, api CWLGetLogEventsAPI, input *cloudwatchlogs.GetLogEventsInput) (*cloudwatchlogs.GetLogEventsOutput, error) {
	return api.GetLogEvents(ctx, input)
}

func main() {

	logGroupName := flag.String("g", "", "The name of the log group")
	logStreamName := flag.String("s", "", "The name of the log stream")
	flag.Parse()

	if *logGroupName == "" || *logStreamName == "" {
		fmt.Println("You must supply a logGroupName and logStreamName")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatchlogs.NewFromConfig(cfg)

	request := &cloudwatchlogs.GetLogEventsInput{
		LogGroupName:  logGroupName,
		LogStreamName: logStreamName,
	}

	response, err := GetEvents(context.TODO(), client, request)
	if err != nil {
		fmt.Println("Could not fetch log events")
		fmt.Println(err)
		return
	}

	fmt.Println("Event messages for stream " + *logStreamName + " in log group  " + *logGroupName)
	for _, event := range response.Events {
		fmt.Println(*event.Message)
	}

}
