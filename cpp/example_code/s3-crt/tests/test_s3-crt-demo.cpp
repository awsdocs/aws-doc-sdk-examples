// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <fstream>
#include <aws/core/Aws.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/FileSystemUtils.h>
#include <aws/s3-crt/S3CrtClient.h>
#include <aws/s3-crt/model/ListObjectsRequest.h>
#include <aws/s3-crt/model/DeleteObjectRequest.h>
#include <aws/s3-crt/model/DeleteBucketRequest.h>
#include <awsdoc/s3-crt/s3-crt-demo.h>

static const char ALLOCATION_TAG[] = "test_s3-crt-demo";

static void Cleanup(const Aws::S3Crt::S3CrtClient& s3CrtClient, const Aws::String& bucketName) {

    std::cout << "Cleaning up bucket: \"" << bucketName << "\" ..." << std::endl;

    Aws::S3Crt::Model::ListObjectsRequest listObjectsRequest;
    listObjectsRequest.SetBucket(bucketName);

    Aws::StringStream errorMessage;
    errorMessage << "Cleanup failed. You will have to cleanup bucket: \"" << bucketName << "\" by yourself." << std::endl;

    Aws::S3Crt::Model::ListObjectsOutcome listObjectsOutcome = s3CrtClient.ListObjects(listObjectsRequest);

    // Empty bucket
    if (!listObjectsOutcome.IsSuccess()) {
        std::cout << errorMessage.str() << std::endl;
        return;
    }
    for (const auto& object : listObjectsOutcome.GetResult().GetContents())
    {
        Aws::S3Crt::Model::DeleteObjectRequest deleteObjectRequest;
        deleteObjectRequest.SetBucket(bucketName);
        deleteObjectRequest.SetKey(object.GetKey());
        Aws::S3Crt::Model::DeleteObjectOutcome deleteObjectOutcome = s3CrtClient.DeleteObject(deleteObjectRequest);
        if (!deleteObjectOutcome.IsSuccess()) {
            std::cout << errorMessage.str() << std::endl;
            return;
        }
    }

    // Delete bucket
    Aws::S3Crt::Model::DeleteBucketRequest deleteBucketRequest;
    deleteBucketRequest.SetBucket(bucketName);
    Aws::S3Crt::Model::DeleteBucketOutcome deleteBucketOutcome = s3CrtClient.DeleteBucket(deleteBucketRequest);
    if (deleteBucketOutcome.IsSuccess()) {
        std::cout << "Bucket cleaned up." << std::endl;
    }
    else {
        std::cout << errorMessage.str() << std::endl;
    }
}

int main()
{
    bool success = true;

    Aws::SDKOptions options;

    options.loggingOptions.crt_logger_create_fn = []() {
        return Aws::MakeShared<Aws::Utils::Logging::DefaultCRTLogSystem>(ALLOCATION_TAG, Aws::Utils::Logging::LogLevel::Warn);
    };
    options.ioOptions.clientBootstrap_create_fn = []() {
        Aws::Crt::Io::EventLoopGroup eventLoopGroup(0 /* cpuGroup */, 18 /* threadCount */);
        Aws::Crt::Io::DefaultHostResolver defaultHostResolver(eventLoopGroup, 8 /* maxHosts */, 300 /* maxTTL */);
        auto clientBootstrap = Aws::MakeShared<Aws::Crt::Io::ClientBootstrap>(ALLOCATION_TAG, eventLoopGroup, defaultHostResolver);
        clientBootstrap->EnableBlockingShutdown();
        return clientBootstrap;
    };

    Aws::InitAPI(options);
    {
        // Create a unique bucket name to increase the chance of success
        // when trying to create the bucket.
        // Format: "my-bucket-" + lowercase UUID.
        Aws::String uuid = Aws::Utils::StringUtils::ToLower(static_cast<Aws::String>(Aws::Utils::UUID::RandomUUID()).c_str());
        Aws::String bucket_name = "my-bucket-" + uuid;
        Aws::String object_key = "my-object-" + uuid;
        Aws::String file_name = "my-file-" + uuid + ".txt";

        Aws::String region = Aws::Region::US_EAST_1;
        Aws::S3Crt::Model::BucketLocationConstraint loc =
            Aws::S3Crt::Model::BucketLocationConstraint::us_east_1;
        const double throughput_target_gbps = 5;
        const uint64_t part_size = 8 * 1024 * 1024; // 8 MB.

        Aws::S3Crt::ClientConfiguration config;
        config.region = region;
        config.throughputTargetGbps = throughput_target_gbps;
        config.partSize = part_size;

        Aws::S3Crt::S3CrtClient s3_crt_client(config);

        if (!ListBuckets(s3_crt_client, bucket_name)) {
            success = false;
        }


        if (success && !CreateBucket(s3_crt_client, bucket_name, loc)) {
            success = false;
        }

        Aws::FStream myFile(file_name.c_str(), std::ios_base::out | std::ios_base::binary);
        myFile << "s3-crt-demo";
        myFile.close();

        if (success && !PutObject(s3_crt_client, bucket_name, object_key, file_name)) {
            Cleanup(s3_crt_client, bucket_name);
            success = false;
        }

        if (success && !GetObject(s3_crt_client, bucket_name, object_key)) {
            Cleanup(s3_crt_client, bucket_name);
            success = false;
        }

        if (success && !DeleteObject(s3_crt_client, bucket_name, object_key)) {
            Cleanup(s3_crt_client, bucket_name);
            success = false;
        }

        if (success && !DeleteBucket(s3_crt_client, bucket_name)) {
            Cleanup(s3_crt_client, bucket_name);
            success = false;
        }

        Aws::FileSystem::RemoveFileIfExists(file_name.c_str());
    }
    Aws::ShutdownAPI(options);

    if (success) {
        return 0;
    }
    else {
        return 1;
    }
}
