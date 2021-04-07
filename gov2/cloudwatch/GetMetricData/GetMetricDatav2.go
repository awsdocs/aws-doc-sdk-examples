package main

import (
	"context"
	"flag"
	"fmt"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
)

// CWGetMetricDataAPI defines the interface for the GetMetricData function
type CWGetMetricDataAPI interface {
	GetMetricData(ctx context.Context, params *cloudwatch.GetMetricDataInput, optFns ...func(*cloudwatch.Options)) (*cloudwatch.GetMetricDataOutput, error)
}

// GetMetrics Fetches the cloudwatch metrics for your provided input in the given time-frame
func GetMetrics(c context.Context, api CWGetMetricDataAPI, input *cloudwatch.GetMetricDataInput) (*cloudwatch.GetMetricDataOutput, error) {
	return api.GetMetricData(c, input)
}

func main() {

	metricName := flag.String("mN", "", "The name of the metric")
	namespace := flag.String("n", "", "The namespace for the metric")
	dimensionName := flag.String("dn", "", "The name of the dimension")
	dimensionValue := flag.String("dv", "", "The value of the dimension")
	id := flag.String("id", "", "A short name used to tie this object to the results in the response")
	diffInMinutes := flag.Int("dM", 0, "The difference in minutes for which the metrics are required")
	stat := flag.String("s", "", "The statistic to to return")
	period := flag.Int("p", 0, "The granularity, in seconds, of the returned data points")
	flag.Parse()

	if *metricName == "" || *namespace == "" || *dimensionName == "" || *dimensionValue == "" || *id == "" || *diffInMinutes == 0 || *stat == "" || *period == 0 {
		fmt.Println("You must supply a metricName, namespace, dimensionName, dimensionValue, id, diffInMinutes, stat, period")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatch.NewFromConfig(cfg)

	input := &cloudwatch.GetMetricDataInput{
		EndTime:   aws.Time(time.Unix(time.Now().Unix(), 0)),
		StartTime: aws.Time(time.Unix(time.Now().Add(time.Duration(-*diffInMinutes)*time.Minute).Unix(), 0)),
		MetricDataQueries: []*cloudwatch.MetricDataQuery{
			&cloudwatch.MetricDataQuery{
				Id: aws.String(*id),
				MetricStat: &cloudwatch.MetricStat{
					Metric: &cloudwatch.Metric{
						Namespace:  aws.String(*namespace),
						MetricName: aws.String(*metricName),
						Dimensions: []*cloudwatch.Dimension{
							&cloudwatch.Dimension{
								Name:  aws.String(*dimensionName),
								Value: aws.String(*dimensionValue),
							},
						},
					},
					Period: aws.Int64(int64(*period)),
					Stat:   aws.String(*stat),
				},
			},
		},
	}

	result, err := GetMetrics(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Could not fetch metric data")
	}

	fmt.Println("Metric Data:", result)

}
