// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/utils/UUID.h>
#include <aws/s3-crt/S3CrtClient.h>
#include <awsdoc/s3-crt/s3-crt-demo.h>

static const char ALLOCATION_TAG[] = "test_s3-crt-demo";

int main()
{
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

        Aws::String region = Aws::Region::US_EAST_1;
        double throughput_target_gbps = 5;
        uint64_t part_size = 5 * 1024 * 1024; // 5 MB.

        Aws::S3Crt::ClientConfiguration config;
        config.region = region;
        config.throughputTargetGbps = throughput_target_gbps;
        config.partSize = part_size;

        Aws::S3Crt::S3CrtClient s3_crt_client(config);

        if (!ListBuckets(s3_crt_client, bucket_name)) {
            return 1;
        }

        if (!CreateBucket(s3_crt_client, bucket_name)) {
            return 1;
        }

        if (!PutObject(s3_crt_client, bucket_name, object_key)) {
            return 1;
        }

        if (!GetObject(s3_crt_client, bucket_name, object_key)) {
            return 1;
        }

        if (!DeleteObject(s3_crt_client, bucket_name, object_key)) {
            return 1;
        }

        if (!DeleteBucket(s3_crt_client, bucket_name)) {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
