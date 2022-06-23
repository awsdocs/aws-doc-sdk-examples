package main

import (
	"context"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/tidwall/gjson"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ec2"
	"github.com/aws/aws-sdk-go-v2/service/ec2/types"
	"github.com/aws/smithy-go"
)

// ---

type Config struct {
	Description string `json:"Description"`
	InstanceID  string `json:"InstanceID"`
	ImageName   string `json:"ImageName"`
	TagName     string `json:"TagName"`
	TagValue    string `json:"TagValue"`
	Monitor     string `json:"Monitor"`
}

var configFileName = "config.json"

var globalConfig Config

func getConfigValue(text string, path string, outval *string) error {
	tValue := gjson.Get(text, path)
	if !tValue.Exists() {
		return errors.New("Failed to find value " + path)
	} else {
		*outval = tValue.String()
		return nil
	}
}

func populateConfiguration(t *testing.T) error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	if err = getConfigValue(text, "InstanceID", &globalConfig.InstanceID); err != nil {
		return err
	}
	if err = getConfigValue(text, "Description", &globalConfig.Description); err != nil {
		return err
	}
	if err = getConfigValue(text, "ImageName", &globalConfig.ImageName); err != nil {
		return err
	}
	if err = getConfigValue(text, "TagName", &globalConfig.TagName); err != nil {
		return err
	}
	if err = getConfigValue(text, "TagValue", &globalConfig.TagValue); err != nil {
		return err
	}
	if err = getConfigValue(text, "Monitor", &globalConfig.Monitor); err != nil {
		return err
	}

	return nil
}

// --

type EC2MockImpl struct{}

func (dt EC2MockImpl) CreateImage(ctx context.Context,
	params *ec2.CreateImageInput,
	optFns ...func(*ec2.Options)) (*ec2.CreateImageOutput, error) {

	output := &ec2.CreateImageOutput{
		ImageId: aws.String("aws-docs-example-imageID"),
	}

	return output, nil
}

func (dt EC2MockImpl) RunInstances(ctx context.Context,
	params *ec2.RunInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.RunInstancesOutput, error) {

	// Create a dummy instance and populate the InstanceId value
	instances := []types.Instance{
		{InstanceId: aws.String("aws-docs-example-instanceID")},
	}

	output := &ec2.RunInstancesOutput{
		Instances: instances,
	}

	return output, nil
}

func (dt EC2MockImpl) CreateTags(ctx context.Context,
	params *ec2.CreateTagsInput,
	optFns ...func(*ec2.Options)) (*ec2.CreateTagsOutput, error) {
	return &ec2.CreateTagsOutput{}, nil
}

type mockDryRunError struct {
	smithy.APIError
}

func (mockDryRunError) ErrorCode() string {
	return "DryRunOperation"
}

func (dt EC2MockImpl) StopInstances(ctx context.Context,
	params *ec2.StopInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.StopInstancesOutput, error) {

	var state types.InstanceState
	state.Name = types.InstanceStateNameStopped

	instances := []types.InstanceStateChange{
		{
			CurrentState: &state,
			InstanceId:   &params.InstanceIds[0],
		},
	}

	output := &ec2.StopInstancesOutput{
		StoppingInstances: instances,
	}

	if *params.DryRun {
		return output, mockDryRunError{}
	}

	return output, nil
}

func (dt EC2MockImpl) MonitorInstances(ctx context.Context,
	params *ec2.MonitorInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.MonitorInstancesOutput, error) {

	// Create a dummy instance for output
	instances := []types.InstanceMonitoring{
		{InstanceId: aws.String("aws-docs-example-instanceID")},
	}

	output := &ec2.MonitorInstancesOutput{
		InstanceMonitorings: instances,
	}

	if *params.DryRun {
		return output, mockDryRunError{}
	}

	return output, nil
}

func (dt EC2MockImpl) UnmonitorInstances(ctx context.Context,
	params *ec2.UnmonitorInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.UnmonitorInstancesOutput, error) {

	// Create a dummy instance for output
	instances := []types.InstanceMonitoring{
		{InstanceId: aws.String("aws-docs-example-instanceID")},
	}

	output := &ec2.UnmonitorInstancesOutput{
		InstanceMonitorings: instances,
	}

	if *params.DryRun {
		return output, mockDryRunError{}
	}

	return output, nil
}

func (dt EC2MockImpl) DescribeVpcEndpointConnections(ctx context.Context,
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

func (dt EC2MockImpl) StartInstances(ctx context.Context,
	params *ec2.StartInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.StartInstancesOutput, error) {

	var state types.InstanceState
	state.Name = types.InstanceStateNameRunning

	instances := []types.InstanceStateChange{
		{CurrentState: &state, InstanceId: &params.InstanceIds[0]},
	}

	output := &ec2.StartInstancesOutput{
		StartingInstances: instances,
	}

	if *params.DryRun {
		return output, mockDryRunError{}
	}

	return output, nil
}

func (dt EC2MockImpl) RebootInstances(ctx context.Context,
	params *ec2.RebootInstancesInput,
	optFns ...func(*ec2.Options)) (*ec2.RebootInstancesOutput, error) {

	output := &ec2.RebootInstancesOutput{}

	if *params.DryRun {
		return output, mockDryRunError{}
	}

	return output, nil
}
