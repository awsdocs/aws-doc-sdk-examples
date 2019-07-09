package main

import (
	"fmt"
	"os"

	aws "github.com/aws/aws-sdk-go/aws"
	session "github.com/aws/aws-sdk-go/aws/session"
	ce "github.com/aws/aws-sdk-go/service/costexplorer"
)

func main() {

	//Must be in YYYY-MM-DD Format
	start := "2019-06-01"
	end := "2019-07-01"
	granularity := "MONTHLY"
	metrics := []string{
		"BlendedCost",
		"UnblendedCost",
		"UsageQuantity",
	}
	// Initialize a session in us-east-1 that the SDK will use to load credentials
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-east-1")},
	)

	// Create Cost Explorer Service Client
	svc := ce.New(sess)

	result, err := svc.GetCostAndUsage(&ce.GetCostAndUsageInput{
		TimePeriod: &ce.DateInterval{
			Start: aws.String(start),
			End:   aws.String(end),
		},
		Granularity: aws.String(granularity),
		GroupBy: []*ce.GroupDefinition{
			&ce.GroupDefinition{
				Type: aws.String("DIMENSION"),
				Key:  aws.String("SERVICE"),
			},
		},
		Metrics: aws.StringSlice(metrics),
	})
	if err != nil {
		exitErrorf("Unable to generate report, %v", err)
	}

	fmt.Println("Cost Report:", result.ResultsByTime)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
