package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

type EC2DescribeInstancesImpl struct{}

func (dt EC2DescribeInstancesImpl) DescribeInstances(ctx context.Context,
	params *ec2.DescribeInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.DescribeInstancesOutput, error) {

	reservations := []types.Reservation{
		{
			ReservationId: aws.String("aws-docs-example-reservationID"),
			Instances: []types.Instance{
				{InstanceId: aws.String("aws-docs-example-instanceID1")},
				{InstanceId: aws.String("aws-docs-example-instanceID2")},
			},
		},
	}

	output := &ec2.DescribeInstancesOutput{
		Reservations: reservations,
	}

	return output, nil
}

func TestDescribeInstances(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	api := &EC2DescribeInstancesImpl{}

	input := &ec2.DescribeInstancesInput{}

	result, err := GetInstances(context.Background(), api, input)
	if err != nil {
		t.Log("Got an error retrieving information about your Amazon EC2 instances:")
		t.Log(err)
		return
	}

	for _, r := range result.Reservations {
		t.Log("Reservation ID: " + *r.ReservationId)
		t.Log("Instance IDs:")
		for _, i := range r.Instances {
			t.Log("   " + *i.InstanceId)
		}

		t.Log("")
	}
}
