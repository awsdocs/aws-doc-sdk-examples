// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[sns_publish_to_topic.go demonstrates how to list, create, and delete a bucket in Amazon S3.]
// snippet-service:[sns]
// snippet-keyword:[Go]
// snippet-keyword:[Amazon SNS]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Publish]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
// snippet-sourceauthor:[AWS]
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
// snippet-start:[sns.go.publish]
package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sns"
)

// usage:
// go run sns_publish_to_topic.go
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2"),
	})

	if err != nil {
		fmt.Println("NewSession error:", err)
		return
	}

	client := sns.New(sess)
	input := &sns.PublishInput{
		Message:  aws.String("Hello world!"),
		TopicArn: aws.String("arn:aws:sns:us-west-2:123456789012:YourTopic"),
	}

	result, err := client.Publish(input)
	if err != nil {
		fmt.Println("Publish error:", err)
		return
	}

	fmt.Println(result)
}

// snippet-end:[sns.go.publish]
