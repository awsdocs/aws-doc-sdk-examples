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
// snippet-start:[s3.go.get_bucket_policy]
package main

// snippet-start:[s3.go.get_bucket_policy.imports]
import (
    "bytes"
    "encoding/json"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.get_bucket_policy.imports]

// RetrieveBucketPolicy retrieves the policy for a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, a byte array containing the policy and nil
//     Otherwise, an empty byte array and an error from the call to GetBucketPolicy
func RetrieveBucketPolicy(sess *session.Session, bucket *string) (bytes.Buffer, error) {
    var dummy bytes.Buffer
    // snippet-start:[s3.go.get_bucket_policy.call]
    svc := s3.New(sess)

    result, err := svc.GetBucketPolicy(&s3.GetBucketPolicyInput{
        Bucket: bucket,
    })
    // snippet-end:[s3.go.get_bucket_policy.call]
    if err != nil {
        return dummy, err
    }

    // snippet-start:[s3.go.get_bucket_policy.string]
    out := bytes.Buffer{}
    policyPtr := aws.StringValue(result.Policy)
    err = json.Indent(&out, []byte(policyPtr), "", "  ")
    // snippet-end:[s3.go.get_bucket_policy.string]
    if err != nil {
        return bytes.Buffer{}, err
    }

    return out, nil
}

func main() {
    // snippet-start:[s3.go.get_bucket_policy.args]
    bucket := flag.String("b", "", "The name of the bucket")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.get_bucket_policy.args]

    // snippet-start:[s3.go.get_bucket_policy.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.get_bucket_policy.args]

    out, err := RetrieveBucketPolicy(sess, bucket)
    if err != nil {
        fmt.Println("Got an error retrieving bucket policy:")
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.get_bucket_policy.print]
    fmt.Println("Policy:")
    fmt.Println(out.String())
    // snippet-end:[s3.go.get_bucket_policy.print]
}
// snippet-end:[s3.go.get_bucket_policy]
