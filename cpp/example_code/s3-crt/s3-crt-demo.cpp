// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <awsdoc/s3-crt/s3-crt-demo.h>
// snippet-start:[s3-crt.cpp.bucket_operations.list_create_delete]
#include <iostream>
#include <fstream>
#include <aws/core/Aws.h>
#include <aws/core/utils/memory/stl/AWSStringStream.h>
#include <aws/core/utils/logging/CRTLogSystem.h>
#include <aws/s3-crt/S3CrtClient.h>
#include <aws/s3-crt/model/CreateBucketRequest.h>
#include <aws/s3-crt/model/DeleteBucketRequest.h>
#include <aws/s3-crt/model/PutObjectRequest.h>
#include <aws/s3-crt/model/GetObjectRequest.h>
#include <aws/s3-crt/model/DeleteObjectRequest.h>

static const char ALLOCATION_TAG[] = "s3-crt-demo";

// List all Amazon S3 buckets under the account.
bool ListBuckets(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName) {

    Aws::S3Crt::Model::ListBucketsOutcome outcome = s3CrtClient.ListBuckets();

    if (outcome.IsSuccess()) {
        std::cout << "All buckets under my account:" << std::endl;

        for (auto const& bucket : outcome.GetResult().GetBuckets())
        {
            std::cout << "  * " << bucket.GetName() << std::endl;
        }
        std::cout << std::endl;

        return true;
    }
    else {
        std::cout << "ListBuckets error:\n"<< outcome.GetError() << std::endl << std::endl;

        return false;
    }
}

// Create an Amazon S3 bucket.
bool CreateBucket(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName) {

    std::cout << "Creating bucket: \"" << bucketName << "\" ..." << std::endl;

    Aws::S3Crt::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3Crt::Model::CreateBucketOutcome outcome = s3CrtClient.CreateBucket(request);

    if (outcome.IsSuccess()) {
        std::cout << "Bucket created." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "CreateBucket error:\n" << outcome.GetError() << std::endl << std::endl;

        return false;
    }
}

// Delete an existing Amazon S3 bucket.
bool DeleteBucket(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName) {

    std::cout << "Deleting bucket: \"" << bucketName << "\" ..." << std::endl;

    Aws::S3Crt::Model::DeleteBucketRequest request;
    request.SetBucket(bucketName);

    Aws::S3Crt::Model::DeleteBucketOutcome outcome = s3CrtClient.DeleteBucket(request);

    if (outcome.IsSuccess()) {
        std::cout << "Bucket deleted." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "DeleteBucket error:\n" << outcome.GetError() << std::endl << std::endl;

        return false;
    }
}

// Put an Amazon S3 object to the bucket.
bool PutObject(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName, const Aws::String& objectKey, const Aws::String& fileName) {

    std::cout << "Putting object: \"" << objectKey << "\" to bucket: \"" << bucketName << "\" ..." << std::endl;

    Aws::S3Crt::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);
    std::shared_ptr<Aws::IOStream> bodyStream = Aws::MakeShared<Aws::FStream>(ALLOCATION_TAG, fileName.c_str(), std::ios_base::in | std::ios_base::binary);
    if (!bodyStream->good()) {
        std::cout << "Failed to open file: \"" << fileName << "\"." << std::endl << std::endl;
        return false;
    }
    request.SetBody(bodyStream);

    Aws::S3Crt::Model::PutObjectOutcome outcome = s3CrtClient.PutObject(request);

    if (outcome.IsSuccess()) {
        std::cout << "Object added." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "PutObject error:\n" << outcome.GetError() << std::endl << std::endl;

        return false;
    }
}

// Get the Amazon S3 object from the bucket.
bool GetObject(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName, const Aws::String& objectKey) {

    std::cout << "Getting object: \"" << objectKey << "\" from bucket: \"" << bucketName << "\" ..." << std::endl;

    Aws::S3Crt::Model::GetObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3Crt::Model::GetObjectOutcome outcome = s3CrtClient.GetObject(request);

    if (outcome.IsSuccess()) {
        std::cout << "Object content: " << outcome.GetResult().GetBody().rdbuf() << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "GetObject error:\n" << outcome.GetError() << std::endl << std::endl;

        return false;
    }
}

// Delete the Amazon S3 object from the bucket.
bool DeleteObject(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName, const Aws::String& objectKey) {

    std::cout << "Deleting object: \"" << objectKey << "\" from bucket: \"" << bucketName << "\" ..." << std::endl;

    Aws::S3Crt::Model::DeleteObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3Crt::Model::DeleteObjectOutcome outcome = s3CrtClient.DeleteObject(request);

    if (outcome.IsSuccess()) {
        std::cout << "Object deleted." << std::endl << std::endl;

        return true;
    }
    else {
        std::cout << "DeleteObject error:\n" << outcome.GetError() << std::endl << std::endl;

        return false;
    }
}

// 1. List all buckets under the account
// 2. Create an Amazon S3 bucket
// 3. Put an object to the bucket
// 4. Get the object
// 5. Delete the object
// 6. Delete the bucket
int main(int argc, char* argv[]) {

    Aws::SDKOptions options;

    // Override default log level for AWS common runtime libraries to prevent from being overwhelmed by logs from them.
    options.loggingOptions.crt_logger_create_fn = []() {
        return Aws::MakeShared<Aws::Utils::Logging::DefaultCRTLogSystem>(ALLOCATION_TAG, Aws::Utils::Logging::LogLevel::Warn);
    };

    // Uncomment the following code to override default global client bootstrap for AWS common runtime libraries.
    // options.ioOptions.clientBootstrap_create_fn = []() {
    //     Aws::Crt::Io::EventLoopGroup eventLoopGroup(0 /* cpuGroup */, 18 /* threadCount */);
    //     Aws::Crt::Io::DefaultHostResolver defaultHostResolver(eventLoopGroup, 8 /* maxHosts */, 300 /* maxTTL */);
    //     auto clientBootstrap = Aws::MakeShared<Aws::Crt::Io::ClientBootstrap>(ALLOCATION_TAG, eventLoopGroup, defaultHostResolver);
    //     clientBootstrap->EnableBlockingShutdown();
    //     return clientBootstrap;
    // };

    // Uncomment the following code to override default global TLS connection options for AWS common runtime libraries.
    // options.ioOptions.tlsConnectionOptions_create_fn = []() {
    //     Aws::Crt::Io::TlsContextOptions tlsCtxOptions = Aws::Crt::Io::TlsContextOptions::InitDefaultClient();
    //     Aws::Crt::Io::TlsContext tlsContext(tlsCtxOptions, Aws::Crt::Io::TlsMode::CLIENT);
    //     return Aws::MakeShared<Aws::Crt::Io::TlsConnectionOptions>(ALLOCATION_TAG, tlsContext.NewConnectionOptions());
    // };

    Aws::InitAPI(options);
    {
        Aws::String bucket_name = "my-bucket";
        Aws::String object_key = "my-object";
        Aws::String file_name = "my-file";
        Aws::String region = Aws::Region::US_EAST_1;
        const double throughput_target_gbps = 5;
        const uint64_t part_size = 8 * 1024 * 1024; // 8 MB.

        Aws::S3Crt::ClientConfiguration config;
        config.region = region;
        config.throughputTargetGbps = throughput_target_gbps;
        config.partSize = part_size;

        Aws::S3Crt::S3CrtClient s3_crt_client(config);

        ListBuckets(s3_crt_client, bucket_name);

        CreateBucket(s3_crt_client, bucket_name);

        PutObject(s3_crt_client, bucket_name, object_key, file_name);

        GetObject(s3_crt_client, bucket_name, object_key);

        DeleteObject(s3_crt_client, bucket_name, object_key);

        DeleteBucket(s3_crt_client, bucket_name);
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3-crt.cpp.bucket_operations.list_create_delete]
