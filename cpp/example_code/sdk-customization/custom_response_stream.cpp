// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <iostream>
#include <fstream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetObjectRequest.h>
#include "sdk_customization_samples.h"

// snippet-start:[cpp.example_code.sdk_customization.CustomResponseStream]
//! Use a custom response stream when downloading an object from an Amazon Simple
//! Storage Service (Amazon S3) bucket.
/*!
  \param bucketName: The Amazon S3 bucket name.
  \param objectKey: The object key.
  \param filePath: File path for custom response stream.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */

bool AwsDoc::SdkCustomization::customResponseStream(const Aws::String &bucketName,
                                                    const Aws::String &objectKey,
                                                    const Aws::String &filePath,
                                                    const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::S3::S3Client s3_client(clientConfiguration);

    Aws::S3::Model::GetObjectRequest getObjectRequest;
    getObjectRequest.WithBucket(bucketName).WithKey(objectKey);

    getObjectRequest.SetResponseStreamFactory([filePath]() {
            return Aws::New<Aws::FStream>(
                    "FStreamAllocationTag", filePath, std::ios_base::out);
    });

    Aws::S3::Model::GetObjectOutcome getObjectOutcome = s3_client.GetObject(
            getObjectRequest);

    if (getObjectOutcome.IsSuccess()) {
        std::cout << "Successfully retrieved object to file " << filePath << std::endl;
    }
    else {
        std::cerr << "Error getting object. "
                  << getObjectOutcome.GetError().GetMessage() << std::endl;
    }

    return getObjectOutcome.IsSuccess();
}
// snippet-end:[cpp.example_code.sdk_customization.CustomResponseStream]


/*
 *
 *  main function
 *
 *  Usage: 'run_custom_response_stream <bucket_name> <object_key> <file_name>'
 *
 *  Prerequisites: An Amazon S3 bucket containing an object.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 4) {
        std::cout << "Usage: run_custom_response_stream <bucket_name> <object_key> <file_name>" << std::endl;
        return 1;
    }

    Aws::String bucketName = argv[1];
    Aws::String keyName = argv[2];
    Aws::String fileName = argv[3];

    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SdkCustomization::customResponseStream(bucketName, keyName, fileName, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

