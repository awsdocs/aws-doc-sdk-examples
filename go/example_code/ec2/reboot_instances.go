package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"
)

// Usage:
// go run main.go <instance id>
func main() {
	// Load session from shared config
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create new EC2 client
	svc := ec2.New(sess)

	// We set DryRun to true to check to see if the instance exists and we have the
	// necessary permissions to monitor the instance.
	input := &ec2.RebootInstancesInput{
		InstanceIds: []*string{
			aws.String(os.Args[1]),
		},
		DryRun: aws.Bool(true),
	}
	result, err := svc.RebootInstances(input)
	awsErr, ok := err.(awserr.Error)

	// If the error code is `DryRunOperation` it means we have the necessary
	// permissions to Start this instance
	if ok && awsErr.Code() == "DryRunOperation" {
		// Let's now set dry run to be false. This will allow us to reboot the instances
		input.DryRun = aws.Bool(false)
		result, err = svc.RebootInstances(input)
		if err != nil {
			fmt.Println("Error", err)
		} else {
			fmt.Println("Success", result)
		}
	} else { // This could be due to a lack of permissions
		fmt.Println("Error", err)
	}
}
