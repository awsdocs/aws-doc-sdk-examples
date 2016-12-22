package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatchevents"
)

// Usage:
// go run main.go
func main() {
	// Load session from shared config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := cloudwatchevents.New(sess)

	result, err := svc.PutTargets(&cloudwatchevents.PutTargetsInput{
		Rule: aws.String("DEMO_EVENT"),
		Targets: []*cloudwatchevents.Target{
			&cloudwatchevents.Target{
				Arn: aws.String("LAMBDA_FUNCTION_ARN"),
				Id:  aws.String("myCloudWatchEventsTarget"),
			},
		},
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
