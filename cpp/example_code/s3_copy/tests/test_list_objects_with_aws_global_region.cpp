// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <awsdoc/s3/s3_list_objects_with_aws_global_region.h>

int main()
{
    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;

    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        config.region = Aws::Region::AWS_GLOBAL;

        Aws::S3::S3Client s3Client(config);

        if (!CreateABucket(s3Client))
        {
            return 1;
        }

        if (!ListTheObjects(s3Client))
        {
            return 1;
        }

        if (!DeleteABucket(s3Client))
        {
            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}