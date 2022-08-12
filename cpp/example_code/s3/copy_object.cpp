//snippet-sourcedescription:[copy_object.cpp demonstrates how to copy an object from one Amazon Simple Storage Service (Amazon S3) bucket to another.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/15/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.cpp.copy_objects.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CopyObjectRequest.h>
#include "awsdoc/s3/s3_examples.h"
// snippet-end:[s3.cpp.copy_objects.inc]

/* 
 *  
 * Prerequisites: Two buckets. One of the buckets must contain the object to 
 * be copied to the other bucket.
 *
 * Inputs:
 * - objectKey: The name of the object to copy.
 * - fromBucket: The name of the bucket to copy the object from.
 * - toBucket: The name of the bucket to copy the object to.
 * - region: The AWS Region to create the bucket in.
 * 
 *  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 * 
 */

 // snippet-start:[s3.cpp.copy_objects.code]

 bool AwsDoc::S3::CopyObject(const Aws::String &objectKey, const Aws::String &fromBucket, const Aws::String &toBucket,
                             const Aws::String &region) {
     Aws::Client::ClientConfiguration clientConfig;
     if (!region.empty()) {
         clientConfig.region = region;
     }

     Aws::S3::S3Client client(clientConfig);
     Aws::S3::Model::CopyObjectRequest request;

     request.WithCopySource(fromBucket + "/" + objectKey)
             .WithKey(objectKey)
             .WithBucket(toBucket);

     Aws::S3::Model::CopyObjectOutcome outcome = client.CopyObject(request);

     if (!outcome.IsSuccess())
     {
         const auto err = outcome.GetError();
         std::cout << "Error: CopyObject: " <<
                   err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
         return false;
     }

     return true;
 }

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    //TODO: Name of object already in bucket.
    Aws::String objectKey = "<enter object key>";

    //TODO: Change from_bucket to the name of your bucket that already contains "my-file.txt".
    Aws::String fromBucket = "<Enter bucket name>";

    //TODO: Change to the name of another bucket in your account.
    Aws::String toBucket = "<Enter bucket name>";

    //TODO: Set to the AWS Region in which the bucket was created.
    Aws::String region = "us-east-1";

    AwsDoc::S3::CopyObject(objectKey, fromBucket, toBucket, region);

    ShutdownAPI(options);
    return 0;
}
// snippet-end:[s3.cpp.copy_objects.code]
