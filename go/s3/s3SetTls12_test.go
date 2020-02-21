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
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"strings"
	"testing"

	"github.com/google/uuid"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"

	"golang.org/x/net/http2"
)

type Config struct {
	Region    string  `json:"Region"`
	Bucket    string  `json:"Bucket"`
	Item      string  `json:"Item"`
	GoVersion float32 `json:"GoVersion"`
	Debug     bool    `json:"Debug"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration() error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.Debug {
		log.Println("Debugging    enabled")
		log.Println("Bucket name: " + globalConfig.Bucket)
		log.Println("Item name:   " + globalConfig.Item)
		log.Println("Region:      " + globalConfig.Region)
		log.Println("GoVersion:   " + fmt.Sprintf("%f", globalConfig.GoVersion))
	}

	return nil
}

func createBucketAndItem(sess *session.Session, bucketName *string, itemName *string) error {
	svc := s3.New(sess)
	_, err := svc.CreateBucket(&s3.CreateBucketInput{
		Bucket: bucketName,
	})
	if err != nil {
		return err
	}

	err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
		Bucket: bucketName,
	})
	if err != nil {
		return err
	}

	_, err = svc.PutObject(&s3.PutObjectInput{
		Body:   strings.NewReader("Hello World!"),
		Bucket: bucketName,
		Key:    itemName,
	})

	return err
}

func deleteBucket(sess *session.Session, bucketName *string) error {
	svc := s3.New(sess)

	iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
		Bucket: bucketName,
	})

	err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
	if err != nil {
		return err
	}

	_, err = svc.DeleteBucket(&s3.DeleteBucketInput{
		Bucket: bucketName,
	})
	if err != nil {
		return err
	}

	return svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
		Bucket: bucketName,
	})
}

func TestBucketCrudOps(t *testing.T) {
	err := populateConfiguration()
	if err != nil {
		log.Fatal("Could not get configuration values")
	}

	bucketName := globalConfig.Bucket
	itemName := globalConfig.Item
	region := globalConfig.Region

	created := false

	if region == "" {
		region = "us-west-2"
	}

	// snippet-start:[s3.go.set_tls_12_transport]
	tr := &http.Transport{
		TLSClientConfig: &tls.Config{
			MinVersion: tls.VersionTLS12,
		},
	}
	// snippet-end:[s3.go.set_tls_12_transport]

	minGo := (float32)(1.12)

	if globalConfig.GoVersion > minGo {
		// snippet-start:[s3.go.set_tls_12_cfg_113]
		tr.ForceAttemptHTTP2 = true
		// snippet-end:[s3.go.set_tls_12_cfg_113]
		log.Println("Created 1.13 config")
	} else {
		// snippet-start:[s3.go.set_tls_12_cfg_112]
		err := http2.ConfigureTransport(tr)
		// snippet-end:[s3.go.set_tls_12_cfg_112]
		log.Println("Created 1.12 config")
		if err != nil {
			log.Fatalf("Failed to configure HTTP transport, %v", err)
		}
	}

	// snippet-start:[s3.go.set_tls_12_session]
	httpClient := http.Client{Transport: tr}

	sess, err := session.NewSession(&aws.Config{
		HTTPClient: &httpClient,
		Region:     &region,
	})
	// snippet-end:[s3.go.set_tls_12_session]
	log.Println("Created HTTP client")
	if err != nil {
		log.Fatalf("Failed to load session, %v", err)
	}

	if bucketName == "" {
		if itemName == "" {
			itemName = "testitem"
		}

		id := uuid.New()
		bucketName = "testbucket-" + id.String()

		log.Println("Using bucket name: " + bucketName)

		// Now create the bucket and item
		err := createBucketAndItem(sess, &bucketName, &itemName)
		if err != nil {
			log.Fatalf("Could not create bucket %v", bucketName)
		}

		created = true
	} else if itemName == "" {
		fmt.Println("You must supply an item name with the bucket name")
		return
	}

	err = ConfirmBucketItemExists(sess, &bucketName, &itemName)
	if err != nil {
		log.Fatalf("Failed to head object, %v", err)
	}

	if created {
		err := deleteBucket(sess, &bucketName)
		if err != nil {
			log.Fatalf("Could not delete bucket %v", bucketName)
		}
	}

	log.Println("Bucket " + bucketName + " and item " + itemName + " can be accessed")
}
