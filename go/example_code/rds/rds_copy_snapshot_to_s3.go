// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[AWS]
// snippet-sourcedescription:[rds_list_cluster_snapshots lists your RDS cluster snapshots.]
// snippet-keyword:[Amazon Relational Database Service]
// snippet-keyword:[Amazon RDS]
// snippet-keyword:[DescribeDBClusterSnapshots function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[rds]
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
// snippet-start:[rds.go.describe_db_cluster_snapshots]
package main

import (
	"fmt"
	"os"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rds"
)

func main() {
	if len(os.Args) != 6 {
		exitErrorf("RoleArn,kmsKey,bucketName,rdsSnapshotName and exportRDSSnapshotName name required\nUsage: %s RoleArn kmsKey bucketName rdsSnapshotName exportRDSSnapshotName", os.Args[0])
	}

	roleArn := os.Args[1]
	kmsKey := os.Args[2]
	bucketName := os.Args[3]
	rdsSnapshotName := os.Args[4]
	exportRDSSnapshotName := os.Args[5]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create RDS service client
	svc := rds.New(sess)
	result, err := svc.DescribeDBSnapshots(nil)
	if err != nil {
		exitErrorf("Unable to list snapshots, %v", err)
	}

	currentTime := time.Now()
	sanpshotTime := currentTime.Format("2006-01-02")
	rdsSnapshot := rdsSnapshotName + sanpshotTime
	IndentifierSanpshotTime := currentTime.Format("20060102")
	exportIdentifier := exportRDSSnapshotName + IndentifierSanpshotTime

	// Getting latest snapsnot from rds snapshot list
	for _, s := range result.DBSnapshots {

		if strings.Contains(*s.DBSnapshotArn, rdsSnapshot) {
			fmt.Printf("* %s with status %s\n",
				aws.StringValue(s.DBSnapshotArn), aws.StringValue(s.Status))

			exportList := []*string{}
			input := &rds.StartExportTaskInput{
				ExportOnly:           exportList, // Optional
				ExportTaskIdentifier: aws.String(exportIdentifier),
				IamRoleArn:           aws.String(roleArn),
				KmsKeyId:             aws.String(kmsKey),
				S3BucketName:         aws.String(bucketName),
				//S3Prefix:             aws.String("/"),  // optional
				SourceArn: aws.String(*s.DBSnapshotArn),
			}

			result, err := svc.StartExportTask(input)

			if err != nil {
				if aerr, ok := err.(awserr.Error); ok {
					switch aerr.Code() {
					case rds.ErrCodeDBSnapshotAlreadyExistsFault:
						fmt.Println(rds.ErrCodeDBSnapshotAlreadyExistsFault, aerr.Error())
					case rds.ErrCodeDBSnapshotNotFoundFault:
						fmt.Println(rds.ErrCodeDBSnapshotNotFoundFault, aerr.Error())
					case rds.ErrCodeInvalidDBSnapshotStateFault:
						fmt.Println(rds.ErrCodeInvalidDBSnapshotStateFault, aerr.Error())
					case rds.ErrCodeSnapshotQuotaExceededFault:
						fmt.Println(rds.ErrCodeSnapshotQuotaExceededFault, aerr.Error())
					case rds.ErrCodeKMSKeyNotAccessibleFault:
						fmt.Println(rds.ErrCodeKMSKeyNotAccessibleFault, aerr.Error())
					default:
						fmt.Println(aerr.Error())
					}
				} else {
					// Print the error, cast err to awserr.Error to get the Code and
					// Message from an error.
					fmt.Println(err.Error())
				}
				return
			}

			fmt.Println(result)

		}

	}

}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
