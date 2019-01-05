//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Using context.Context with SDK requests.]
//snippet-keyword:[Extending the SDK]
//snippet-keyword:[Go]
//snippet-service:[aws-go-sdk]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[2018-03-16]
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

    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    // SQS ReceiveMessage
    params := &sqs.ReceiveMessageInput{ ... }
    req, resp := s.ReceiveMessageRequest(params)
    req.HTTPRequest = req.HTTPRequest.WithContext(ctx)
    err := req.Send()
