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

	// Create the cloudwatch events client
	svc := cloudwatchevents.New(sess)

	result, err := svc.PutEvents(&cloudwatchevents.PutEventsInput{
		Entries: []*cloudwatchevents.PutEventsRequestEntry{
			&cloudwatchevents.PutEventsRequestEntry{
				Detail:     aws.String("{ \"key1\": \"value1\", \"key2\": \"value2\" }"),
				DetailType: aws.String("appRequestSubmitted"),
				Resources: []*string{
					aws.String("RESOURCE_ARN"),
				},
				Source: aws.String("com.company.myapp"),
			},
		},
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", result)
}
