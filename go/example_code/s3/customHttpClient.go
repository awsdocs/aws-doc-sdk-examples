// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[customHttpClient.go gets an item from an S3 bucket using a custom HTTP client.]
// snippet-service:[Go SDK]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-keyword:[AWS S3]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-4-26]
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
    "bytes"
    "flag"
    "fmt"
    "net"
    "net/http"
    "os"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"

    "golang.org/x/net/http2"
)

// snippet-start:[s3.go.customHttpClient_struct]
type HTTPClientSettings struct {
    Connect          time.Duration
    ConnKeepAlive    time.Duration
    ExpectContinue   time.Duration
    IdleConn         time.Duration
    MaxAllIdleConns  int
    MaxHostIdleConns int
    ResponseHeader   time.Duration
    TLSHandshake     time.Duration
}
// snippet-end:[s3.go.customHttpClient_struct]

// snippet-start:[s3.go.customHttpClient_client]
func NewHTTPClientWithSettings(httpSettings HTTPClientSettings) *http.Client {
    tr := &http.Transport{
        ResponseHeaderTimeout: httpSettings.ResponseHeader,
        Proxy:                 http.ProxyFromEnvironment,
        DialContext:           (&net.Dialer{
            KeepAlive: httpSettings.ConnKeepAlive,
            DualStack: true,
            Timeout:   httpSettings.Connect,
        }).DialContext,
        MaxIdleConns:          httpSettings.MaxAllIdleConns,
        IdleConnTimeout:       httpSettings.IdleConn,
        TLSHandshakeTimeout:   httpSettings.TLSHandshake,
        MaxIdleConnsPerHost:   httpSettings.MaxHostIdleConns,
        ExpectContinueTimeout: httpSettings.ExpectContinue,
    }

    // So client makes HTTP/2 requests
    http2.ConfigureTransport(tr)

    return &http.Client{
        Transport: tr,
    }
}
// snippet-end:[s3.go.customHttpClient_client]

// Create a custom HTTP client and uses it to get an S3 bucket item.
//
// Usage:
//    go run customHttpClient -b bucket -i item [-s]
func main() {
    bucketPtr := flag.String("b", "", "The name of the bucket")
    itemPtr := flag.String("i", "", "The name of the bucket item")
    regionPtr := flag.String("r", "us-west-2", "The region")
    showPtr := flag.Bool("s", false, "Whether to show the bucket item as a string")
    flag.Parse()
    bucket := *bucketPtr
    item := *itemPtr
    region := *regionPtr
    show := *showPtr

    if bucket == "" || item == "" {
        fmt.Println("You must supply the name of the bucket and item")
        fmt.Println("Usage: go run customHttpClient -b bucket-name -i item-name [-s] (show the bucket item as a string)")
        os.Exit(1)
    }

    fmt.Println("Getting item " + item + " from bucket " + bucket + " in " + region)

    // Creating a SDK session using the custom HTTP client
    // and use that session to create S3 client.
    // snippet-start:[s3.go.customHttpClient_session]
    sess := session.Must(session.NewSession(&aws.Config{
        Region: regionPtr,
        HTTPClient: NewHTTPClientWithSettings(HTTPClientSettings{
            Connect:          5 * time.Second,
            ExpectContinue:   1 * time.Second,
            IdleConn:         90 * time.Second,
            ConnKeepAlive:    30 * time.Second,
            MaxAllIdleConns:  100,
            MaxHostIdleConns: 10,
            ResponseHeader:   5 * time.Second,
            TLSHandshake:     5 * time.Second,
        }),
    }))

    client := s3.New(sess)
    // snippet-end:[s3.go.customHttpClient_session]

    /* If you are only using one client,
     * you could use the custom HTTP client when you create the client object:
     *
     * sess := session.Must(session.NewSession())

     * client := s3.New(sess, &aws.Config{
     *     Region: region,
     *        HTTPClient: NewHTTPClientWithSettings(HTTPClientSettings{
     *            Connect:          5 * time.Second,
     *            ExpectContinue:   1 * time.Second,
     *            IdleConn:         90 * time.Second,
     *            KeepAlive:        30 * time.Second,
     *            MaxAllIdleConns:  100,
     *            MaxHostIdleConns: 10,
     *            ResponseHeader:   5 * time.Second,
     *            TLSHandshake:     5 * time.Second,
     *        }),
     * })
     *
     */

    obj, err := client.GetObject(&s3.GetObjectInput{
        Bucket: bucketPtr,
        Key:    itemPtr,
    })
    if err != nil {
        fmt.Println("Got error calling GetObject:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    if show {
        // Convert body from IO.ReadCloser to string:
        buf := new(bytes.Buffer)
        buf.ReadFrom(obj.Body)
        newBytes := buf.String()
        s := string(newBytes)

        fmt.Println("Bucket item as string:")
        fmt.Println(s)
    } else {
        fmt.Println("Got bucket item using custom HTTP client")
    }
}
// snippet-end:[s3.go.customHttpClient]
