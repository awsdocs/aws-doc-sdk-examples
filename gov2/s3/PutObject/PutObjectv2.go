// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.PutObject]
package main

// snippet-start:[s3.go-v2.PutObject.imports]
import (
	"context"
	"flag"
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// snippet-end:[s3.go-v2.PutObject.imports]

// S3PutObjectAPI defines the interface for the PutObject function.
// snippet-start:[s3.go-v2.PutObject.interface]
type S3PutObjectAPI interface {
	PutObject(ctx context.Context,
		params *s3.PutObjectInput,
		optFns ...func(*s3.Options)) (*s3.PutObjectOutput, error)
}

// snippet-end:[s3.go-v2.PutObject.interface]

// PutFile uploads a file to a bucket
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a PutObjectOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to Open or PutObject
func PutFile(c context.Context, api S3PutObjectAPI, input *s3.PutObjectInput) (*s3.PutObjectOutput, error) {
	// snippet-start:[s3.go-v2.PutObject.call]
	resp, err := api.PutObject(c, input)
	// snippet-end:[s3.go-v2.PutObject.call]

	return resp, err
}

func main() {
	// snippet-start:[s3.go-v2.upload_object.args]
	bucket := flag.String("b", "", "The bucket to upload the file to")
	filename := flag.String("f", "", "The file to upload")
	flag.Parse()

	if *bucket == "" || *filename == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET) and file name (-f FILE)")
		return
	}
	// snippet-end:[s3.go-v2.upload_object.args]

	// snippet-start:[s3.go-v2.PutObject.configclient]
	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)
	// snippet-end:[s3.go-v2.PutObject.configclient]

	// snippet-start:[s3.go-v2.PutObject.open]
	file, err := os.Open(*filename)
	// snippet-end:[s3.go-v2.PutObject.open]
	if err != nil {
		fmt.Println("Unable to open file " + *filename)
		return
	}

	defer file.Close()

	// snippet-start:[s3.go-v2.PutObject.call]

	input := &s3.PutObjectInput{
		Bucket: bucket,
		Key:    filename,
		Body:   file,
	}

	_, err = PutFile(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got error uploading file:")
		fmt.Println(err)
		return
	}
}

// snippet-end:[s3.go-v2.upload_object]
