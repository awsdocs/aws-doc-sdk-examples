//snippet-sourcedescription:[put_object.cpp demonstrates how to asynchronously put a file into an Amazon S3 bucket.]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[2019-04-19]
//snippet-sourceauthor:[AWS]

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

// snippet-start:[s3.cpp.put_object_async.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <chrono>
#include <condition_variable>
#include <fstream>
#include <iostream>
#include <mutex>
#include <sys/stat.h>
#include <thread>
// snippet-end:[s3.cpp.put_object_async.inc]

/**
 * Check if file exists
 *
 * Note: If using C++17, can use std::filesystem::exists()
 */
inline bool file_exists(const std::string& name)
{
    struct stat buffer;
    return (stat(name.c_str(), &buffer) == 0);
}

/**
 * Function called when PutObjectAsync() finishes
 *
 * The thread that started the async operation is waiting for notification 
 * that the operation has finished. A std::condition_variable is used to 
 * communicate between the two threads.
*/
// snippet-start:[s3.cpp.put_object_async.mutex_vars]
std::mutex upload_mutex;
std::condition_variable upload_variable;
// snippet-end:[s3.cpp.put_object_async.mutex_vars]

// snippet-start:[s3.cpp.put_object_async_finished.code]
void put_object_async_finished(const Aws::S3::S3Client* client, 
    const Aws::S3::Model::PutObjectRequest& request, 
    const Aws::S3::Model::PutObjectOutcome& outcome,
    const std::shared_ptr<const Aws::Client::AsyncCallerContext>& context)
{
    // Output operation status
    if (outcome.IsSuccess()) {
        std::cout << "put_object_async_finished: Finished uploading " 
            << context->GetUUID() << std::endl;
    }
    else {
        auto error = outcome.GetError();
        std::cout << "ERROR: " << error.GetExceptionName() << ": "
            << error.GetMessage() << std::endl;
    }

    // Notify waiting function
    upload_variable.notify_one();
}
// snippet-end:[s3.cpp.put_object_async_finished.code]

/**
 * Asynchronously put an object into an Amazon S3 bucket
 */
// snippet-start:[s3.cpp.put_object_async.code]
bool put_s3_object_async(const Aws::S3::S3Client& s3_client,
    const Aws::String& s3_bucket_name,
    const Aws::String& s3_object_name,
    const std::string& file_name)
{
    // Verify file_name exists
    if (!file_exists(file_name)) {
        std::cout << "ERROR: NoSuchFile: The specified file does not exist"
            << std::endl;
        return false;
    }

    // Set up request
    Aws::S3::Model::PutObjectRequest object_request;

    object_request.SetBucket(s3_bucket_name);
    object_request.SetKey(s3_object_name);
    const std::shared_ptr<Aws::IOStream> input_data =
        Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
            file_name.c_str(),
            std::ios_base::in | std::ios_base::binary);
    object_request.SetBody(input_data);
    auto context =
        Aws::MakeShared<Aws::Client::AsyncCallerContext>("PutObjectAllocationTag");
    context->SetUUID(s3_object_name);

    // Put the object asynchronously
    s3_client.PutObjectAsync(object_request, 
                             put_object_async_finished,
                             context);
    return true;
    // snippet-end:[s3.cpp.put_object_async.code]
}

/**
 * Exercise put_s3_object_async()
 */
int main(int argc, char** argv)
{

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Assign these values before running the program
        const Aws::String bucket_name = "BUCKET_NAME";
        const Aws::String object_name = "OBJECT_NAME";
        const std::string file_name = "FILE_NAME_TO_UPLOAD";
        const Aws::String region = "";      // Optional

        // If a region is specified, use it
        Aws::Client::ClientConfiguration clientConfig;
        if (!region.empty())
            clientConfig.region = region;

        // snippet-start:[s3.cpp.put_object_async.invoke.code]
        // NOTE: The S3Client object that starts the async operation must 
        // continue to exist until the async operation completes.
        Aws::S3::S3Client s3Client(clientConfig);

        // Put the file into the S3 bucket asynchronously
        std::unique_lock<std::mutex> lock(upload_mutex);
        if (put_s3_object_async(s3Client, 
                                bucket_name, 
                                object_name, 
                                file_name)) {
            // While the upload is in progress, we can perform other tasks.
            // For this example, we just wait for the upload to finish.
            std::cout << "main: Waiting for file upload to complete..." 
                << std::endl;
            upload_variable.wait(lock);

            // The upload has finished. The S3Client object can be cleaned up 
            // now. We can also terminate the program if we wish.
            std::cout << "main: File upload completed" << std::endl;
        }
        // snippet-end:[s3.cpp.put_object_async.invoke.code]
    }
    Aws::ShutdownAPI(options);
}
