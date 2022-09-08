//snippet-sourcedescription:[delete_bucket_policy.cpp demonstrates how to delete a policy from an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.cpp.delete_bucket_policy.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketPolicyRequest.h>
#include "awsdoc/s3/s3_examples.h"
// snippet-end:[s3.cpp.delete_bucket_policy.inc]

/* 
 *  
 * Prerequisites: The bucket containing the bucket policy to be deleted.
 *
 * Inputs:
 * - bucketName: The name of the bucket containing the bucket policy to delete.
 * - region: The AWS Region of the bucket.
 * 
 *  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 * 
 */
 
 // snippet-start:[s3.cpp.delete_bucket_policy.code]

bool AwsDoc::S3::DeleteBucketPolicy(const Aws::String &bucketName, const Aws::String &region) {
    Aws::Client::ClientConfiguration clientConfig;
    if (!region.empty()) {
        clientConfig.region = region;
    }

    Aws::S3::S3Client client(clientConfig);

    Aws::S3::Model::DeleteBucketPolicyRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::DeleteBucketPolicyOutcome outcome =  client.DeleteBucketPolicy(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteBucketPolicy: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }
    else
    {
        std::cout << "Policy was deleted from the bucket." << std::endl;
        return true;
    }
}

int main()
{
    //TODO: Change bucket_name to the name of a bucket in your account.
    const Aws::String bucketName = "<Enter bucket name>";
    //TODO: Set to the AWS Region in which the bucket was created.
    const Aws::String region = "us-east-1";


    Aws::SDKOptions options;
    Aws::InitAPI(options);

    AwsDoc::S3::DeleteBucketPolicy(bucketName, region);

    ShutdownAPI(options);
}
// snippet-end:[s3.cpp.delete_bucket_policy.code]
