// snippet-sourceauthor:[tokiwong]
// snippet-sourcedescription:[Retrieves cost and usage metrics for your account]
// snippet-keyword:[Amazon Cost Explorer]
// snippet-keyword:[Amazon CE]
// snippet-keyword:[GetCostAndUsage function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[ce]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-07-09]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/costexplorer"
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
	svc := costexplorer.New(sess)

	result, err := svc.GetCostAndUsage(&costexplorer.GetCostAndUsageInput{
		TimePeriod: &costexplorer.DateInterval{
			Start: aws.String(start),
			End:   aws.String(end),
		},
		Granularity: aws.String(granularity),
		GroupBy: []*costexplorer.GroupDefinition{
			&costexplorer.GroupDefinition{
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
