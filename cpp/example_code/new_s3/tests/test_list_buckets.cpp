/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

#include <awsdoc/s3/s3_examples.h>
#include <aws/core/Aws.h>

int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (!AwsDoc::S3::ListBuckets())
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
