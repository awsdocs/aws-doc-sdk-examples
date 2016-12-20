package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"
)

// Usage:
// go run main.go
func main() {
	// Load session from shared config
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create new EC2 client
	svc := ec2.New(sess)

	// Retrieves all regions/endpoints that work with EC2
	resultRegions, err := svc.DescribeRegions(nil)
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", resultRegions.Regions)

	// Retrieves availability zones only for region of the ec2 service object
	resultAvalZones, err := svc.DescribeAvailabilityZones(nil)
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Success", resultAvalZones.AvailabilityZones)
}
