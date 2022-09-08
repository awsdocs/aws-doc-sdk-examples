//snippet-sourcedescription:[create_bucket.cpp demonstrates how to creates an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/15/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.cpp.create_bucket.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/BucketLocationConstraint.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include "awsdoc/s3/s3_examples.h"
// snippet-end:[s3.cpp.create_bucket.inc]

/* 
 * Input:
 * - bucketName: The name of the bucket to create. 
 * - region: The AWS Region to create the bucket in.
 * 
 *  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 */
 
// snippet-start:[s3.cpp.create_bucket.code]

bool AwsDoc::S3::CreateBucket(const Aws::String &bucketName, const Aws::String &region) {
    // Create the bucket.
    Aws::Client::ClientConfiguration clientConfig;
    if (!region.empty())
        clientConfig.region = region;

    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::CreateBucketRequest request;
    request.SetBucket(bucketName);


    //TODO(user): Change the bucket location constraint enum to your target Region.
    Aws::S3::Model::CreateBucketConfiguration createBucketConfig;
    createBucketConfig.SetLocationConstraint(Aws::S3::Model::BucketLocationConstraint::us_east_1);
    request.SetCreateBucketConfiguration(createBucketConfig);

    Aws::S3::Model::CreateBucketOutcome outcome = client.CreateBucket(request);
    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: CreateBucket: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        return false;
    }
    else
    {
        std::cout << "Created bucket " << bucketName <<
                  " in the specified AWS Region." << std::endl;
        return true;
    }

}
int main()
{
    Aws::SDKOptions options;
    InitAPI(options);
    //TODO(user): Set to the AWS Region of your account.  If you don't, you will get a runtime
    //IllegalLocationConstraintException Message: "The unspecified location constraint is incompatible
    //for the Region specific endpoint this request was sent to."
    Aws::String region = "us-east-1"; // The Region for your Amazon S3 client.

    // Create a unique bucket name to increase the chance of success 
    // when trying to create the bucket.
    // Format: "doc-example-bucket-" + lowercase UUID.
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    Aws::String bucketName = "doc-example-bucket-" +
                             Aws::Utils::StringUtils::ToLower(uuid.c_str());

    AwsDoc::S3::CreateBucket(bucketName, region);
    ShutdownAPI(options);
}
// snippet-end:[s3.cpp.create_bucket.code]
