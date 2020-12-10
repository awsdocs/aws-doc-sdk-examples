package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/aws/aws-sdk-go/aws"
)

type S3ListBucketsImpl struct{}

func (dt S3ListBucketsImpl) ListBuckets(ctx context.Context,
	params *s3.ListBucketsInput,
	optFns ...func(*s3.Options)) (*s3.ListBucketsOutput, error) {

	// Create a dummy list of two buckets
	buckets := make([]*types.Bucket, 2)
	buckets[0] = &types.Bucket{Name: aws.String("bucket1")}
	buckets[1] = &types.Bucket{Name: aws.String("bucket2")}

	output := &s3.ListBucketsOutput{
		Buckets: buckets,
	}

	return output, nil
}

func TestListBuckets(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// Build the request with its input parameters
	input := s3.ListBucketsInput{}

	api := &S3ListBucketsImpl{}

	resp, err := GetAllBuckets(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Got", len(resp.Buckets), "buckets:")
	for _, b := range resp.Buckets {
		t.Log("  " + *b.Name)
	}
}
