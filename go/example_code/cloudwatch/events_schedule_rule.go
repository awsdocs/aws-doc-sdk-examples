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

	result, err := svc.PutRule(&cloudwatchevents.PutRuleInput{
		Name:               aws.String("DEMO_EVENT"),
		RoleArn:            aws.String("IAM_ROLE_ARN"),
		ScheduleExpression: aws.String("rate(5 minutes)"),
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
