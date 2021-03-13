// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[neo7337]
// snippet-sourcedescription:[Retrieves a list of published AWS CloudWatch metrics.]
// snippet-keyword:[AWS CloudWatch]
// snippet-keyword:[GetMetricData function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[cloudwatch]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2021-03-13]

package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"
)

// Fetches the cloudwatch metrics for your provided input in the given time-frame

func main() {
	if len(os.Args) != 9 {
		fmt.Println("You must supply a metric name, namespace, dimension name, dimension value, id, start time, end time, stat and period")
		os.Exit(1)
	}

	metricName := os.Args[1] //case sensitive
	namespace := os.Args[2]
	dimensionName := os.Args[3]
	dimensionValue := os.Args[4]
	id := os.Args[5]
	//The parameter EndTime must be greater than StartTime
	startTime := os.Args[6] //time.Unix(time.Now().Add(time.Duration(-60)*time.Minute).Unix(), 0)
	endTime := os.Args[7]   //time.Unix(time.Now().Unix(), 0)
	stat := os.Args[8]
	period := os.Args[9]

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create CloudWatch client
	svc := cloudwatch.New(sess)

	// Invoke the GetMetricData
	result, err := svc.GetMetricData(&cloudwatch.GetMetricDataInput{
		EndTime:   aws.Time(startTime),
		StartTime: aws.Time(endTime),
		MetricDataQueries: []*cloudwatch.MetricDataQuery{
			&cloudwatch.MetricDataQuery{
				Id: aws.String(id),
				MetricStat: &cloudwatch.MetricStat{
					Metric: &cloudwatch.Metric{
						Namespace:  aws.String(namespace),
						MetricName: aws.String(metricName),
						Dimensions: []*cloudwatch.Dimension{
							&cloudwatch.Dimension{
								Name:  aws.String(dimensionName),
								Value: aws.String(dimensionValue),
							},
						},
					},
					Period: aws.Int64(period),
					Stat:   aws.String(stat),
				},
			},
		},
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Metrics", result)
}
