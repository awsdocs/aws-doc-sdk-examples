// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[put_object_with_setters.go shows how to use API field setters with SDK requests.]
// snippet-keyword:[Extending the SDK]
// snippet-keyword:[PutObject function]
// snippet-keyword:[Go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-22]
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
//snippet-start:[s3.go.put_object]
package main

import (
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    
    "strings"
)

func main() {
    // Initialize a session that loads
    // credentials from the shared credentials file ~/.aws/credentials.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create S3 service client
    svc := s3.New(sess)

    //snippet-start:[s3.go.put_object.call]
    svc.PutObject((&s3.PutObjectInput{}).
        SetBucket("myBucket").
        SetKey("myKey").
        SetBody(strings.NewReader("object body")).
        SetWebsiteRedirectLocation("https://example.com/something"),
    )
    //snippet-end:[s3.go.put_object.call]
    //snippet-end:[s3.go.put_object]
}
