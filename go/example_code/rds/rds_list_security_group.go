// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[AWS]
// snippet-sourcedescription:[rds_list_security_groups.go lists your RDS security groups.]
// snippet-keyword:[Amazon Relational Database Service]
// snippet-keyword:[Amazon RDS]
// snippet-keyword:[DescribeDBSecurityGroups function]
// snippet-keyword:[Go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
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
// snippet-start:[rds.go.describe_db_security_groups]
package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rds"
)

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create RDS service client
	svc := rds.New(sess)

	result, err := svc.DescribeDBSecurityGroups(nil)
	if err != nil {
		exitErrorf("Unable to list security groups, %v", err)
	}

	for _, s := range result.DBSecurityGroups {
		fmt.Printf("* %s in VpcId: %s\n",
			aws.StringValue(s.DBSecurityGroupName), aws.StringValue(s.VpcId))
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
// snippet-end:[rds.go.describe_db_security_groups]
