// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

#include <iostream>
#include "awsdoc/s3/s3_examples.h"
#include <aws/core/Aws.h>

int main()
{
    Aws::SDKOptions options;
    InitAPI(options);

    int result = 0;
    if (!AwsDoc::S3::ListBucketDisablingDnsCache())
    {
        result = 1;
    }

    ShutdownAPI(options);
    return result;
}