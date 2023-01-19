/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to delete multiple objects in an Amazon Simple Storage
 * Service (Amazon S3) bucket.
 *
 */

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectsRequest.h>
#include "awsdoc/s3/s3_examples.h"

//! Routine which demonstrates deleting multiple objects in an Amazon S3 bucket.
/*!
  \sa DeleteObjects()
  \param objectKeys: Vector of object keys.
  \param fromBucket: Name of a bucket with an object to delete.
  \param clientConfig: AWS client configuration.
*/

// snippet-start:[cpp.example_code.s3.delete_objects]
bool AwsDoc::S3::DeleteObjects(const std::vector<Aws::String> &objectKeys,
                               const Aws::String &fromBucket,
                               const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteObjectsRequest request;

    Aws::S3::Model::Delete deleteObject;
    for (const Aws::String& objectKey : objectKeys)
    {
        deleteObject.AddObjects(Aws::S3::Model::ObjectIdentifier().WithKey(objectKey));
    }

    request.SetDelete(deleteObject);
    request.SetBucket(fromBucket);

    Aws::S3::Model::DeleteObjectsOutcome outcome =
            client.DeleteObjects(request);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error deleting objects. " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully deleted the objects.";
        for (size_t i = 0; i < objectKeys.size(); ++i)
        {
            std::cout << objectKeys[i];
            if (i < objectKeys.size() - 1)
            {
                std::cout << ", ";
            }
        }

        std::cout << " from bucket " << fromBucket << "." << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.s3.delete_objects]

/*
 *
 * main function
 *
 * Prerequisites: The bucket containing the objects to delete.
 *
 * Usage: 'run_delete_objects <bucket_name> <object_key> ...'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {

    if (argc < 3)
    {
        std::cout << R"(
Usage:
   run_delete_objects <bucket_name> <object_key> ...
Where:
   bucket_name - Name of S3 bucket.
   <object_key> ... - One or more objects to delete.
)" << std::endl;

        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String bucketName(argv[1]);
        Aws::Vector<Aws::String> objectKeys;

        for (int i = 2; i < argc; ++i)
        {
            objectKeys.push_back(Aws::String(argv[i]));
        }

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::DeleteObjects(objectKeys, bucketName, clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
