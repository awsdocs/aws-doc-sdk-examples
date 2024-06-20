// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <chrono>
#include <condition_variable>
#include <fstream>
#include <iostream>
#include <mutex>
#include <sys/stat.h>
#include "s3_examples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to put an object in an S3 bucket using the async API.
  *
 */

// snippet-start:[s3.cpp.put_object_async.mutex_vars]
// A mutex is a synchronization primitive that can be used to protect shared
// data from being simultaneously accessed by multiple threads.
std::mutex AwsDoc::S3::upload_mutex;

// A condition_variable is a synchronization primitive that can be used to
// block a thread, or to block multiple threads at the same time.
// The thread is blocked until another thread both modifies a shared
// variable (the condition) and notifies the condition_variable.
std::condition_variable AwsDoc::S3::upload_variable;
// snippet-end:[s3.cpp.put_object_async.mutex_vars]

//! Routine which implements an async task finished callback.
/*!
  \fn putObjectAsyncFinished()
  \param s3Client Instance of the caller's Amazon S3 client object.
  \param request Instance of the caller's put object request.
  \param outcome Instance of the caller's put object outcome.
  \param context Instance of the caller's put object call context.
*/

// snippet-start:[s3.cpp.put_object_async_finished.code]
void putObjectAsyncFinished(const Aws::S3::S3Client *s3Client,
                            const Aws::S3::Model::PutObjectRequest &request,
                            const Aws::S3::Model::PutObjectOutcome &outcome,
                            const std::shared_ptr<const Aws::Client::AsyncCallerContext> &context) {
    if (outcome.IsSuccess()) {
        std::cout << "Success: putObjectAsyncFinished: Finished uploading '"
                  << context->GetUUID() << "'." << std::endl;
    } else {
        std::cerr << "Error: putObjectAsyncFinished: " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    // Unblock the thread that is waiting for this function to complete.
    AwsDoc::S3::upload_variable.notify_one();
}
// snippet-end:[s3.cpp.put_object_async_finished.code]


//! Routine which demonstrates adding an object to an Amazon S3 bucket, asynchronously.
/*!
  \param s3Client Instance of the S3 Client.
  \param bucketName Name of the bucket.
  \param fileName Name of the file to put in the bucket.
*/

// snippet-start:[s3.cpp.put_object_async.code]
bool AwsDoc::S3::putObjectAsync(const Aws::S3::S3Client &s3Client,
                                const Aws::String &bucketName,
                                const Aws::String &fileName) {
    // Create and configure the asynchronous put object request.
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(fileName);

    const std::shared_ptr<Aws::IOStream> input_data =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                          fileName.c_str(),
                                          std::ios_base::in | std::ios_base::binary);

    if (!*input_data) {
        std::cerr << "Error: unable to open file " << fileName << std::endl;
        return false;
    }

    request.SetBody(input_data);

    // Create and configure the context for the asynchronous put object request.
    std::shared_ptr<Aws::Client::AsyncCallerContext> context =
            Aws::MakeShared<Aws::Client::AsyncCallerContext>("PutObjectAllocationTag");
    context->SetUUID(fileName);

    // Make the asynchronous put object call. Queue the request into a 
    // thread executor and call the putObjectAsyncFinished function when the
    // operation has finished. 
    s3Client.PutObjectAsync(request, putObjectAsyncFinished, context);

    return true;
}
// snippet-end:[s3.cpp.put_object_async.code]

/**
 *
 * main function
 *
 * Prerequisites: The bucket and the object to get the ACL information about:
 *
 * Usage: run_put_object_async <file_name> <bucket_name>
 *
 */

#ifndef TESTING_BUILD

// snippet-start:[s3.cpp.put_object_async.invoke.code]
int main(int argc, char* argv[])
{
    if (argc != 3)
    {
        std::cout << R"(
Usage:
    run_put_object_async <file_name> <bucket_name>
Where:
    file_name - The name of the file to upload.
    bucket_name - The name of the bucket to upload the object to.
)" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String fileName = argv[1];
        const Aws::String bucketName = argv[2];

        // A unique_lock is a general-purpose mutex ownership wrapper allowing
        // deferred locking, time-constrained attempts at locking, recursive
        // locking, transfer of lock ownership, and use with
        // condition variables.
        std::unique_lock<std::mutex> lock(AwsDoc::S3::upload_mutex);

        // Create and configure the Amazon S3 client.
        // This client must be declared here, as this client must exist
        // until the put object operation finishes.
        Aws::S3::S3ClientConfiguration config;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // config.region = "us-east-1";

        Aws::S3::S3Client s3Client(config);

        AwsDoc::S3::putObjectAsync(s3Client, bucketName, fileName);

        std::cout << "main: Waiting for file upload attempt..." <<
                  std::endl << std::endl;

        // While the put object operation attempt is in progress,
        // you can perform other tasks.
        // This example simply blocks until the put object operation
        // attempt finishes.
        AwsDoc::S3::upload_variable.wait(lock);

        std::cout << std::endl << "main: File upload attempt completed."
                  << std::endl;
    }
    Aws::ShutdownAPI(options);

    return 0;
}

// snippet-end:[s3.cpp.put_object_async.invoke.code]

#endif  // TESTING_BUILD
