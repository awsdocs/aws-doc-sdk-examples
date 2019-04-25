// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[customHttpClient.go gets an item from an S3 bucket using a custom HTTP client.]
// snippet-service:[Go SDK]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-4-25]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[s3.go.customHttpClient]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"

    "bytes"
    "flag"
    "fmt"
    "io"
    "net"
    "net/http"
    "os"
    "time"
)

// snippet-start:[s3.go.customHttpClient_struct]
type HttpClientSettings struct {
    Connect          time.Duration
    ExpectContinue   time.Duration
    IdleConn         time.Duration
    KeepAlive        time.Duration
    MaxAllIdleConns  int
    MaxHostIdleConns int
    ResponseHeader   time.Duration
    TLSHandshake     time.Duration
}
// snippet-end:[s3.go.customHttpClient_struct]

// snippet-start:[s3.go.customHttpClient_client]
func NewHTTPClientWithTimeouts(httpSettings HttpClientSettings) *http.Client {
    return &http.Client{
        Transport: &http.Transport{
            ResponseHeaderTimeout: httpSettings.ResponseHeader,
            Proxy:                 http.ProxyFromEnvironment,
            DialContext:           (&net.Dialer{
                KeepAlive: httpSettings.KeepAlive,
                DualStack: true,
                Timeout:   httpSettings.Connect,
            }).DialContext,
            MaxIdleConns:          httpSettings.MaxAllIdleConns,
            IdleConnTimeout:       httpSettings.IdleConn,
            TLSHandshakeTimeout:   httpSettings.TLSHandshake,
            MaxIdleConnsPerHost:   httpSettings.MaxHostIdleConns,
            ExpectContinueTimeout: httpSettings.ExpectContinue,
        },
    }
}
// snippet-end:[s3.go.customHttpClient_client]

// snippet-start:[s3.go.customHttpClient_s3_client]
func ExampleS3WithCustomHTTPClient(bucket, key, region *string) io.ReadCloser {
    // Creating a SDK session using the SDK's default HTTP client,
    // http.DefaultClient.
    sess := session.Must(session.NewSession())

    // Create SDK S3 client with a HTTP client configured for custom timeouts.
    client := s3.New(sess, &aws.Config{
        Region:     region,
        HTTPClient: NewHTTPClientWithTimeouts(HttpClientSettings{
            Connect:            5 * time.Second,
            ExpectContinue:     1 * time.Second,
            IdleConn:          90 * time.Second,
            KeepAlive:         30 * time.Second,
            MaxAllIdleConns:  100,
            MaxHostIdleConns:  10,
            ResponseHeader:     5 * time.Second,
            TLSHandshake:       5 * time.Second,
        }),
    })

    obj, err := client.GetObject(&s3.GetObjectInput{
        Bucket: bucket,
        Key:    key,
    })
    if err != nil {
        fmt.Println("Got error calling GetObject in ExampleS3WithCustomHTTPClient:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    return obj.Body
}
// snippet-end:[s3.go.customHttpClient_s3_client]

// snippet-start:[s3.go.customHttpClient_session]
func exampleSharedClient(bucket, key, region *string) io.ReadCloser {
    // Create a shared SDK session to be used by all SDK clients.
    // All SDK clients share the HTTP client's timeout configuration.
    sess := session.Must(session.NewSession(&aws.Config{
        Region: region,
        HTTPClient: NewHTTPClientWithTimeouts(HttpClientSettings{
            Connect:          5 * time.Second,
            ExpectContinue:   1 * time.Second,
            IdleConn:         90 * time.Second,
            KeepAlive:        30 * time.Second,
            MaxAllIdleConns:  100,
            MaxHostIdleConns: 10,
            ResponseHeader:   5 * time.Second,
            TLSHandshake:     5 * time.Second,
        }),
    }))

    // Create an S3 SDK client with the shared SDK session,
    // which includes the same HTTP custom timeouts.
    client := s3.New(sess)

    // Make API operation calls with SDK clients, all sharing the same HTTP client timeout configuration.
    obj, err := client.GetObject(&s3.GetObjectInput{
        Bucket: bucket,
        Key:    key,
    })
    if err != nil {
        fmt.Println("Got error calling GetObject in exampleSharedClient:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    return obj.Body
}
// snippet-end:[s3.go.customHttpClient_session]

// Create a custom HTTP client and uses it to list your S3 buckets.
//
// Usage:
//    go run customHttpClient -b bucket -k key [-s]
func main() {
    bucketPtr := flag.String("b", "", "The name of the bucket")
    keyPtr := flag.String("k", "", "The name of the key")
    regionPtr := flag.String("r", "us-west-2", "The region")
    showPtr := flag.Bool("s", false, "Whether to show the key as a string")
    flag.Parse()
    bucket := *bucketPtr
    key := *keyPtr
    show := *showPtr

    if bucket == "" || key == "" {
        fmt.Println("You must supply the name of the bucket and key")
        fmt.Println("Usage: go run customHttpClient -b bucket-name -k key-name [-s] (show the key as a string)")
        os.Exit(1)
    }

    fmt.Println("Getting key " + key + " from bucket " + bucket)

    // Get object using custom HTTP client
    if show {
        body := ExampleS3WithCustomHTTPClient(bucketPtr, keyPtr, regionPtr)

        // Convert body from IO.ReadCloser to string:
        buf := new(bytes.Buffer)
        buf.ReadFrom(body)
        newBytes := buf.String()
        s := string(newBytes)

        fmt.Println(s)
    } else {
        ExampleS3WithCustomHTTPClient(bucketPtr, keyPtr, regionPtr)
        fmt.Println("Got object using custom HTTP client")
    }

    // Get object using shared custom HTTP session
    if show {
        body := exampleSharedClient(bucketPtr, keyPtr, regionPtr)
        // Convert body from IO.ReadCloser to string:
        buf := new(bytes.Buffer)
        buf.ReadFrom(body)
        newBytes := buf.String()

        s := string(newBytes)

        fmt.Println(s)
    } else {
        exampleSharedClient(bucketPtr, keyPtr, regionPtr)
        fmt.Println("Got object using shared client")
    }
}

// snippet-end:[s3.go.customHttpClient]
