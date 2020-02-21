// snippet-comment:[The snippet tags are for the AWS SDK for Go Developer Guide. Do not remove.]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[s3.go.set_tls_12_transport]
package main

import (
	"crypto/tls"
	"flag"
	"fmt"
	"net/http"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"golang.org/x/net/http2"
)

// ConfirmBucketItemExists returns nil if the bucket and item can be accessed
func ConfirmBucketItemExists(sess *session.Session, bucket *string, item *string) error {
	// snippet-start:[s3.go.set_tls_12_client]
	svc := s3.New(sess)
	_, err := svc.HeadObject(&s3.HeadObjectInput{
		Bucket: bucket,
		Key:    item,
	})
	// snippet-end:[s3.go.set_tls_12_client]
	if err != nil {
		return err
	}

	return nil
}

func main() {
	bucketNamePtr := flag.String("b", "", "The bucket to check")
	itemNamePtr := flag.String("i", "", "The bucket item to check")
	regionPtr := flag.String("r", "us-west-2", "The region where the bucket lives")
	goV112Ptr := flag.Bool("v", false, "Whether the Go version is prior to 1.13")
	flag.Parse()

	if *bucketNamePtr == "" || *itemNamePtr == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET-NAME) and item name (-i ITEM-NAME)")
		return
	}

	// snippet-start:[s3.go.set_tls_12_transport]
	tr := &http.Transport{
		TLSClientConfig: &tls.Config{
			MinVersion: tls.VersionTLS12,
		},
	}
	// snippet-end:[s3.go.set_tls_12_transport]

	if *goV112Ptr {
		// snippet-start:[s3.go.set_tls_12_cfg_112]
		err := http2.ConfigureTransport(tr)
		if err != nil {
			fmt.Println("Got an error configuring HTTP transport")
			fmt.Println(err)
			return
		}
		// snippet-end:[s3.go.set_tls_12_cfg_112]
	} else {
		// snippet-start:[s3.go.set_tls_12_cfg_113]
		tr.ForceAttemptHTTP2 = true
		// snippet-end:[s3.go.set_tls_12_cfg_113]
	}

	// snippet-start:[s3.go.set_tls_12_session]
	client := http.Client{Transport: tr}

	sess, err := session.NewSession(&aws.Config{
		HTTPClient: &client,
		Region:     regionPtr,
	})
	// snippet-end:[s3.go.set_tls_12_session]        
	if err != nil {
		fmt.Println("Got an error creating new session:")
		fmt.Println(err)
		return
	}

	err = ConfirmBucketItemExists(sess, bucketNamePtr, itemNamePtr)
	if err != nil {
		fmt.Print("Could not confirm whether bucket and item exists")
		return
	}

	fmt.Println("Bucket " + *bucketNamePtr + " and item " + *itemNamePtr + " can be accessed")
}
