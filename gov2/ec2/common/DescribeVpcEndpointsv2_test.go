package main

import (
	"context"
	"encoding/json"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/ec2"
)

type EC2DescribeVpcEndpointConnectionsImpl struct{}

func TestDescribeVpcEndpointConnections(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	api := &EC2MockImpl{}

	input := &ec2.DescribeVpcEndpointConnectionsInput{}

	resp, err := GetConnectionInfo(context.Background(), api, input)
	if err != nil {
		t.Fatal(err)
	}

	t.Log("VPC endpoint: Details:")
	respDecrypted, _ := json.MarshalIndent(resp, "", "\t")
	t.Log(string(respDecrypted))
}
