/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <fstream>
#include <sys/stat.h>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include "awsdoc/s3/s3_examples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to put an object in an S3 bucket.
  *
 */

//! Routine which demonstrates putting an object in an S3 bucket.
/*!
  \fn PutObject()
  \param bucketName Name of the bucket.
  \param fileName Name of the file to put in the bucket.
  \param clientConfig Aws client configuration.
*/

// snippet-start:[s3.cpp.put_object.code]
bool AwsDoc::S3::PutObject(const Aws::String &bucketName,
                           const Aws::String &fileName,
                           const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    //We are using the name of the file as the key for the object in the bucket.
    //However, this is just a string and can be set according to your retrieval needs.
    request.SetKey(fileName);

    std::shared_ptr<Aws::IOStream> inputData =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                          fileName.c_str(),
                                          std::ios_base::in | std::ios_base::binary);

    if (!*inputData) {
        std::cerr << "Error unable to read file " << fileName << std::endl;
        return false;
    }

    request.SetBody(inputData);

    Aws::S3::Model::PutObjectOutcome outcome =
            s3_client.PutObject(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: PutObject: " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Added object '" << fileName << "' to bucket '"
                  << bucketName << "'.";
    }

    return outcome.IsSuccess();
}
// snippet-end:[s3.cpp.put_object.code]

/**
 *
 * main function
 *
 * TODO(User) items: Set the following variables
 * - bucketName: The name of the bucket.
 * - fileName: The name of the file to add.
 *
*/

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO(User): Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "<Enter bucket name>";
        //TODO(User): Create a file called "my-file.txt" in the local folder where your executables are built to.
        const Aws::String object_name = "<Enter file>";

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::PutObject(bucket_name, object_name, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif  // TESTING_BUILD
