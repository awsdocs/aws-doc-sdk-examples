/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
// snippet-sourcedescription:[s3.go demonstrates how to list, create, and delete a bucket in Amazon S3.]
// snippet-service:[s3]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ListBuckets]
// snippet-keyword:[CreateBucket]
// snippet-keyword:[DeleteBucket]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2017-07-11]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.go.bucket_operations.list_create_delete]
package main

import (
  "fmt"
  "os"
  "github.com/aws/aws-sdk-go/aws"
  "github.com/aws/aws-sdk-go/aws/session"
  "github.com/aws/aws-sdk-go/service/s3"
)

func main() {

  if len(os.Args) < 3 {
    fmt.Printf("Usage: go run s3.go <the bucket name> <the AWS Region to use>\n" +
      "Example: go run s3.go my-test-bucket us-east-2\n")
    os.Exit(1)
  }

  sess := session.Must(session.NewSessionWithOptions(session.Options{
    SharedConfigState: session.SharedConfigEnable,
  }))
  svc := s3.New(sess, &aws.Config{
    Region: aws.String(os.Args[2]),
  })

  listMyBuckets(svc)
  createMyBucket(svc, os.Args[1], os.Args[2])
  listMyBuckets(svc)
  deleteMyBucket(svc, os.Args[1])
  listMyBuckets(svc)
}

// List all of your available buckets in this AWS Region.
func listMyBuckets(svc *s3.S3) {
  result, err := svc.ListBuckets(nil)

  if err != nil {
    exitErrorf("Unable to list buckets, %v", err)
  }

  fmt.Println("My buckets now are:\n")

  for _, b := range result.Buckets {
    fmt.Printf(aws.StringValue(b.Name) + "\n")
  }

  fmt.Printf("\n")
}

// Create a bucket in this AWS Region.
func createMyBucket(svc *s3.S3, bucketName string, region string) {
  fmt.Printf("\nCreating a new bucket named '" + bucketName + "'...\n\n")

  _, err := svc.CreateBucket(&s3.CreateBucketInput{
   Bucket: aws.String(bucketName),
   CreateBucketConfiguration: &s3.CreateBucketConfiguration{
     LocationConstraint: aws.String(region),
   },
 })

  if err != nil {
    exitErrorf("Unable to create bucket, %v", err)
  }
  
  // Wait until bucket is created before finishing
  fmt.Printf("Waiting for bucket %q to be created...\n", bucketName)

  err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
    Bucket: aws.String(bucketName),
  })
}

// Delete the bucket you just created.
func deleteMyBucket(svc *s3.S3, bucketName string) {
  fmt.Printf("\nDeleting the bucket named '" + bucketName + "'...\n\n")

  _, err := svc.DeleteBucket(&s3.DeleteBucketInput{
    Bucket: aws.String(bucketName),
  })

  if err != nil {
    exitErrorf("Unable to delete bucket, %v", err)
  }
  
  // Wait until bucket is deleted before finishing
  fmt.Printf("Waiting for bucket %q to be deleted...\n", bucketName)

  err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
    Bucket: aws.String(bucketName),
  })
}

// If there's an error, display it.
func exitErrorf(msg string, args ...interface{}) {
  fmt.Fprintf(os.Stderr, msg+"\n", args...)
  os.Exit(1)
}
// snippet-end:[s3.go.bucket_operations.list_create_delete]
