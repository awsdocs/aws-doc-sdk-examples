// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[kinesis.go-v2.PutRecord]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/kinesis"
)

// KinesisPutRecordAPI defines the interface for the PutRecord function.
// We use this interface to test the function using a mocked service.
type KinesisPutRecordAPI interface {
	PutRecord(ctx context.Context,
		params *kinesis.PutRecordInput,
		optFns ...func(*kinesis.Options))  (*kinesis.PutRecordOutput, error)
}

// MakePutRecord creates an Amazon Kinesis (Amazon Kinesis) stream record.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a PutRecordOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to PutRecordOutput.
func MakePutRecord(c context.Context, api KinesisPutRecordAPI, input *kinesis.PutRecordInput) (*kinesis.PutRecordOutput, error) {
	return api.PutRecord(c, input)
}

func main() {
	stream := flag.String("s", "", "The name of the stream")
	partition := flag.String("k", "", "The identifier of the partition key")
	payload := flag.String("p", "", "The payload")
	flag.Parse()

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := kinesis.NewFromConfig(cfg)

	input := &kinesis.PutRecordInput{
		Data: []byte(*payload),
		PartitionKey: partition,
		StreamName: stream,
	}

	results, err := MakePutRecord(context.TODO(), client, input)
	if err != nil {
		fmt.Println(err.Error())
	}

	fmt.Println(results.SequenceNumber)
}

// snippet-end:[kinesis.go-v2.PutRecord]