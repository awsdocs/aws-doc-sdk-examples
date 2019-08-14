 
//snippet-sourcedescription:[create_bucket.cpp demonstrates how to create an Amazon S3 bucket.]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[2019-06-20]
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

//snippet-start:[s3.cpp.create_bucket.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
//snippet-end:[s3.cpp.create_bucket.inc]

/**
 * Create an Amazon S3 bucket in a specified region
 */
// snippet-start:[s3.cpp.create_bucket.code]
bool create_bucket(const Aws::String &bucket_name,
    const Aws::S3::Model::BucketLocationConstraint &region = Aws::S3::Model::BucketLocationConstraint::us_east_1)
{
    // Set up the request
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucket_name);

    // Is the region other than us-east-1 (N. Virginia)?
    if (region != Aws::S3::Model::BucketLocationConstraint::us_east_1)
    {
        // Specify the region as a location constraint
        Aws::S3::Model::CreateBucketConfiguration bucket_config;
        bucket_config.SetLocationConstraint(region);
        request.SetCreateBucketConfiguration(bucket_config);
    }

    // Create the bucket
    Aws::S3::S3Client s3_client;
    auto outcome = s3_client.CreateBucket(request);
    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "ERROR: CreateBucket: " << 
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        return false;
    }
    return true;
}
// snippet-end:[s3.cpp.create_bucket.code]

/**
 * Exercise create_bucket()
 */
int main()
{

    // Set these values before compiling and running the program
    const Aws::String bucket_name_in_default_region = "BUCKET_NAME";
    const Aws::String bucket_name_in_specified_region = "BUCKET_NAME";
    const Aws::S3::Model::BucketLocationConstraint region = 
        Aws::S3::Model::BucketLocationConstraint::us_west_2;

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Create a bucket in the S3 default region (us-east-1)
        if (create_bucket(bucket_name_in_default_region))
        {
            std::cout << "Created bucket " << bucket_name_in_default_region <<
                " in the S3 default region (us-east-1)\n";
        }

        // Create a bucket in a specified region
        if (create_bucket(bucket_name_in_specified_region, region))
        {
            std::cout << "Created bucket " << bucket_name_in_specified_region <<
                " in the specified region" << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
}
