/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/Object.h>
#include <awsdoc/s3/s3_examples.h>

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to list the objects in an S3 bucket.
 *
 */

//! Routine which demonstrates listing the objects in an S3 bucket.
/*!
  \fn ListObjects()
  \param bucketName Name of the S3 bucket.
  \param clientConfig Aws client configuration.
 */

// snippet-start:[s3.cpp.list_objects.code]
bool AwsDoc::S3::ListObjects(const Aws::String &bucketName,
                             const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::ListObjectsRequest request;
    request.WithBucket(bucketName);

    auto outcome = s3_client.ListObjects(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: ListObjects: " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        Aws::Vector<Aws::S3::Model::Object> objects =
                outcome.GetResult().GetContents();

        for (Aws::S3::Model::Object &object: objects) {
            std::cout << object.GetKey() << std::endl;
        }
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.list_objects.code]

/*
 *
 *   main function
 *
 * Prerequisites: Create a bucket containing at least one object.
 *
 * TODO(user): items: Set the following variables.
 * - bucketName: The name of the bucket containing the objects.
 *
 */

#ifndef TESTING_BUILD

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO(user): Name of a bucket in your account.
        //The bucket must have at least one object in it.  One way to achieve
        //this is to configure and run put_object.cpp's executable first.
        const Aws::String bucket_name = "my-bucket-2f2730dd-0f5d-4dfa-b55d-8d36a3bfea39";

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::ListObjects(bucket_name, clientConfig);
     }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif  // TESTING_BUILD

