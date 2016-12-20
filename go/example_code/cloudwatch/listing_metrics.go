package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"
)

// Usage:
// go run main.go
func main() {
	// Load session from shared config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create new cloudwatch client.
	svc := cloudwatch.New(sess)

	// This will disable the alarm.
	result, err := svc.ListMetrics(&cloudwatch.ListMetricsInput{
		MetricName: aws.String("IncomingLogEvents"),
		Namespace:  aws.String("AWS/Logs"),
		Dimensions: []*cloudwatch.DimensionFilter{
			&cloudwatch.DimensionFilter{
				Name: aws.String("LogGroupName"),
			},
		},
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Metrics", result.Metrics)
}
