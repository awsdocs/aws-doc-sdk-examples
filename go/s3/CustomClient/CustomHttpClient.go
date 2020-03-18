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
// snippet-start:[s3.go.customHttpClient]
package main

// snippet-start:[s3.go.customHttpClient.import]
import (
    "bytes"
    "context"
    "flag"
    "fmt"
    "io"
    "net"
    "net/http"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"

    "golang.org/x/net/http2"
)

// snippet-end:[s3.go.customHttpClient.import]

// HTTPClientSettings defines the HTTP setting for clients
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

// NewHTTPClientWithSettings creates an HTTP client with some custom settings
// Inputs:
//     httpSettings contains some custom HTTP settings for the client
// Output:
//     If success, an HTTP client
//     Otherwise, ???
// snippet-start:[s3.go.customHttpClient_client]
func NewHTTPClientWithSettings(httpSettings HTTPClientSettings) (*http.Client, error) {
    var client http.Client
    tr := &http.Transport{
        ResponseHeaderTimeout: httpSettings.ResponseHeader,
        Proxy:                 http.ProxyFromEnvironment,
        DialContext: (&net.Dialer{
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
    err := http2.ConfigureTransport(tr)
    if err != nil {
        return &client, err
    }

    return &http.Client{
        Transport: tr,
    }, nil
}

// snippet-end:[s3.go.customHttpClient_client]

// GetObjectWithTimeout retrieves an S3 bucket object, but only within 20 seconds
// Inputs:
//     bucket is the name of the S3 bucket
//     object is the name of the S3 bucket object
// Output:
//     If success, the content of the object and nil
//     Otherwise, an empty object and an error from the call to GetObjectWithContext
func GetObjectWithTimeout(sess *session.Session, bucket *string, object *string) (io.ReadCloser, error) {
    var body io.ReadCloser
    svc := s3.New(sess)

    // snippet-start:[s3.go.customHttpClient.get_object]
    ctx, cancelFn := context.WithTimeout(context.TODO(), 20*time.Second)
    defer cancelFn()

    resp, err := svc.GetObjectWithContext(ctx, &s3.GetObjectInput{
        Bucket: bucket,
        Key:    object,
    })
    if err != nil {
        return body, err
    }

    return resp.Body, nil
    // snippet-end:[s3.go.customHttpClient.get_object]
}

// Create a custom HTTP client and uses it to get an S3 bucket item
// or get it using a custom timeout of 20 seconds.
//
// Usage:
//    go run customHttpClient -b BUCKET-NAME -o OBJECT-NAME [-s] [-t]
func main() {
    // snippet-start:[s3.go.customHttpClient.args]
    bucket := flag.String("b", "", "The name of the bucket")
    object := flag.String("o", "", "The name of the bucket object")
    show := flag.Bool("s", false, "Whether to show the object as a string")
    timeout := flag.Bool("t", false, "Whether to get the object within a 20 second timeout or use the default custom HTTP client")
    flag.Parse()

    if *bucket == "" || *object == "" {
        fmt.Println("You must supply the name of the bucket and object")
        fmt.Println("Usage: go run customHttpClient -b BUCKET -o OBJECT [-s] [-t]")
        return
    }
    // snippet-end:[s3.go.customHttpClient.args]

    fmt.Println("Getting object " + *object + " from bucket " + *bucket)

    var body io.ReadCloser
    var err error

    if *timeout {
        // Initialize a session that the SDK uses to load
        // credentials from the shared credentials file (~/.aws/credentials)
        // snippet-start:[s3.go.customHttpClient.sess]
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))
        // snippet-end:[s3.go.customHttpClient.sess]

        // Get object using 20 second timeout
        body, err = GetObjectWithTimeout(sess, bucket, object)
        if err != nil {
            fmt.Println("Could not get " + *object + " from " + *bucket)
            return
        }

        fmt.Println("Got " + *object + " from " + *bucket)
    } else {
        // Creating a SDK session using the custom HTTP client
        // and use that session to create S3 client.
        // snippet-start:[s3.go.customHttpClient_session]
        httpClient, err := NewHTTPClientWithSettings(HTTPClientSettings{
            Connect:          5 * time.Second,
            ExpectContinue:   1 * time.Second,
            IdleConn:         90 * time.Second,
            ConnKeepAlive:    30 * time.Second,
            MaxAllIdleConns:  100,
            MaxHostIdleConns: 10,
            ResponseHeader:   5 * time.Second,
            TLSHandshake:     5 * time.Second,
        })
        if err != nil {
            fmt.Println("Got an error creating custom HTTP client:")
            fmt.Println(err)
            return
        }

        sess := session.Must(session.NewSession(&aws.Config{
            HTTPClient: httpClient,
        }))

        svc := s3.New(sess)
        // snippet-end:[s3.go.customHttpClient_session]

        /* If you are only using one client,
           * you could use the custom HTTP client when you create the client object:
           *
             sess := session.Must(session.NewSession())

             client := s3.New(sess, &aws.Config{
                 HTTPClient: NewHTTPClientWithSettings(HTTPClientSettings{
                     Connect:          5 * time.Second,
                     ExpectContinue:   1 * time.Second,
                     IdleConn:         90 * time.Second,
                     KeepAlive:        30 * time.Second,
                     MaxAllIdleConns:  100,
                     MaxHostIdleConns: 10,
                     ResponseHeader:   5 * time.Second,
                     TLSHandshake:     5 * time.Second,
                 }),
             })
           *
        */

        obj, err := svc.GetObject(&s3.GetObjectInput{
            Bucket: bucket,
            Key:    object,
        })
        if err != nil {
            fmt.Println("Got error calling GetObject:")
            fmt.Println(err.Error())
            return
        }

        body = obj.Body
    }

    if *show {
        // Convert body from IO.ReadCloser to string:
        buf := new(bytes.Buffer)

        _, err := buf.ReadFrom(body)
        if err != nil {
            fmt.Println("Got an error reading body of object:")
            fmt.Println(err)
            return
        }

        newBytes := buf.String()
        s := string(newBytes)

        fmt.Println("Bucket object as string:")
        fmt.Println(s)
    }
}

// snippet-end:[s3.go.customHttpClient]
