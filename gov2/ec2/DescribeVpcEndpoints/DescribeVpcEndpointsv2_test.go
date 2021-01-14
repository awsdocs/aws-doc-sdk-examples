package main

import (
	"context"
	"encoding/json"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
)

type EC2DescribeVpcEndpointConnectionsImpl struct{}

func (dt EC2DescribeVpcEndpointConnectionsImpl) DescribeVpcEndpointConnections(ctx context.Context,
	params *ec2.DescribeVpcEndpointConnectionsInput,
	optFns ...func(*ec2.Options)) (*ec2.DescribeVpcEndpointConnectionsOutput, error) {
	connections := make([]types.VpcEndpointConnection, 1)

	dns1 := types.DnsEntry{
		DnsName:      aws.String("vpce-01a0bb23c4dd55ef6-ghijklmn.vpce-svc-0a9876b5c54dd2e10.us-west-2.vpce.amazonaws.com"),
		HostedZoneId: aws.String("Z9YXW88VVUTSR"),
	}

	dns2 := types.DnsEntry{
		DnsName:      aws.String("vpce-01a0bb23c4dd55ef6-ghijklmn-us-west-2b.vpce-svc-0a9876b5c54dd2e10.us-west-2.vpce.amazonaws.com"),
		HostedZoneId: aws.String("Z9YXW88VVUTSR"),
	}

	dns3 := types.DnsEntry{
		DnsName:      aws.String("vpce-01a0bb23c4dd55ef6-ghijklmn-us-west-2c.vpce-svc-0a9876b5c54dd2e10.us-west-2.vpce.amazonaws.com"),
		HostedZoneId: aws.String("Z9YXW88VVUTSR"),
	}

	dnsEntries := make([]types.DnsEntry, 3)
	dnsEntries[0] = dns1
	dnsEntries[1] = dns2
	dnsEntries[2] = dns3

	lbArns := make([]string, 1)
	lbArns[0] = "arn:aws:elasticloadbalancing:us-east-1:188580781645:loadbalancer/net/mysfits-nlb/f6540d1733b1a04d"

	con1 := types.VpcEndpointConnection{
		CreationTimestamp: aws.Time(time.Now()),
		DnsEntries:        dnsEntries,
		//		GatewayLoadBalancerArns: [],
		NetworkLoadBalancerArns: lbArns,
		ServiceId:               aws.String("vpce-svc-0f9494c5c97ee9a02"),
		VpcEndpointId:           aws.String("vpce-05d0ee78a7cc88ef2"),
		VpcEndpointOwner:        aws.String("392220576650"),
		VpcEndpointState:        types.StateAvailable,
	}

	connections[0] = con1

	output := &ec2.DescribeVpcEndpointConnectionsOutput{
		VpcEndpointConnections: connections,
	}

	return output, nil
}

func TestMETHOD(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	api := &EC2DescribeVpcEndpointConnectionsImpl{}

	input := &ec2.DescribeVpcEndpointConnectionsInput{}

	resp, err := GetConnectionInfo(context.TODO(), api, input)
	if err != nil {
		t.Fatal(err)
	}

	t.Log("VPC endpoint: Details:")
	respDecrypted, _ := json.MarshalIndent(resp, "", "\t")
	t.Log(string(respDecrypted))
}
