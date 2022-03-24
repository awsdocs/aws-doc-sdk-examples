package main

import (
	"context"
	"fmt"
	"log"
	"testing"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/rs/xid"
)

var client *s3.Client
var bucketName string

func init() {
	log.Println("Setting up suite")

	bucketName = "mybucket-" + (xid.New().String())
	fmt.Printf("Bucket name: %v\n", bucketName)

	cfg, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		panic("Failed to load configuration")
	}

	client = s3.NewFromConfig(cfg)

}

func TestOps(t *testing.T) {

	t.Log("Creating bucket...")
	MakeBucket(*client, bucketName)
	t.Log("Doing things to the bucket...")
	BucketOps(*client, bucketName)
	t.Log("list and such things being done to the bucket...")
	AccountBucketOps(*client, bucketName)
	t.Log("Cleaning up the bucket...")
	BucketDelOps(*client, bucketName)

}
