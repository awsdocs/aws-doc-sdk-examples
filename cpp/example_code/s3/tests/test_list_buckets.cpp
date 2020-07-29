// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <awsdoc/s3/s3_examples.h>

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // List the available buckets.
        if (!AwsDoc::S3::ListBuckets())
        {
            return 1;
        }
    }
    ShutdownAPI(options);

    return 0;
}