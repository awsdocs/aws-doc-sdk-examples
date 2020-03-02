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
	"errors"
	"fmt"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"reflect"
	"strconv"
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
	Test      bool    `json:"Test"`
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

	log.Println("Bucket name: " + globalConfig.Bucket)
	log.Println("Item name:   " + globalConfig.Item)
	log.Println("Region:      " + globalConfig.Region)
	log.Println("GoVersion:   " + fmt.Sprintf("%f", globalConfig.GoVersion))

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

func tlsVersion() (int, error) {
	// Create fake dialer to localhost so we can get conn
	c, err := net.Dial("tcp", "127.0.0.1:666")
	if err != nil {
		return 0, err
	}

	cv := reflect.ValueOf(c)
	switch ce := cv.Elem(); ce.Kind() {
	case reflect.Struct:
		fe := ce.FieldByName("vers")
		return int(fe.Uint()), nil
	}

	return 0, errors.New("something wrong")
}

func TestTLSVersion(t *testing.T) {
	err := populateConfiguration()
	if err != nil {
		log.Fatal("Could not get configuration values")
	}

	// Create HTTP client with minimum TLS version
	tr := &http.Transport{
		TLSClientConfig: &tls.Config{
			MinVersion: tls.VersionTLS12,
		},
	}

	minGo := (float32)(1.12)

	if globalConfig.GoVersion > minGo {
		tr.ForceAttemptHTTP2 = true
		t.Log("Created TLS 1.2 for Go version 1.13")
	} else {
		err := http2.ConfigureTransport(tr)
		t.Log("Created TLS 1.12 for Go version 1.12 (or previous)")
		if err != nil {
			t.Fatal(err)
		}
	}

	// Create an HTTP client with the configured transport.
	client := http.Client{Transport: tr}

	// Create the SDK's session with the custom HTTP client.
	sess, err := session.NewSession(&aws.Config{
		HTTPClient: &client,
	})
	if err != nil {
		t.Fatal(err)
	}

	if globalConfig.Test {
		version, err := tlsVersion() // *tls.Conn)
		if err != nil {
			t.Log("Got error calling tlsVersion:")
			t.Fatal(err)
		}

		t.Log("Your TLS version using reflection: " + strconv.Itoa(version))
	} else {
		// snippet-start:[s3.go.get_tls_version_call]
		version := GetTLSVersion(tr)

		t.Log("Your TLS version: " + version)
		// snippet-end:[s3.go.get_tls_version_call]
	}

	created := false

	// Set region, bucket, item values if not supplied
	defaultRegion := "us-west-2"
	if globalConfig.Region == "" {
		t.Log("Setting region to " + defaultRegion)
		globalConfig.Region = defaultRegion
	}

	if globalConfig.Bucket == "" {
		if globalConfig.Item == "" {
			globalConfig.Item = "testitem"
		}

		id := uuid.New()
		globalConfig.Bucket = "testbucket-" + id.String()

		// Create the bucket and item
		err := createBucketAndItem(sess, &globalConfig.Bucket, &globalConfig.Item)
		if err != nil {
			t.Fatal(err)
		}

		t.Log("Created bucket: " + globalConfig.Bucket)
		t.Log("With item :     " + globalConfig.Item)

		created = true
	} else if globalConfig.Item == "" {
		fmt.Println("You must supply an item name with the bucket name")
		return
	}

	err = ConfirmBucketItemExists(sess, &globalConfig.Bucket, &globalConfig.Item)
	if err != nil {
		t.Fatal(err)
	}

	if created {
		err := deleteBucket(sess, &globalConfig.Bucket)
		if err != nil {
			t.Fatal(err)
		}
	}

	t.Log("Bucket " + globalConfig.Bucket + " and item " + globalConfig.Item + " can be accessed")
}
