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
