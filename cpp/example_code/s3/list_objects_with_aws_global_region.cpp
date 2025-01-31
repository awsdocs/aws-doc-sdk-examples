// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <thread>
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/HeadBucketRequest.h>
#include <aws/s3/model/ListObjectsV2Request.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/core/utils/UUID.h>
#include "s3_examples.h"

/**
 * Before running this C++ code example, set up your development environment,
 * including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to list objects specifying
 * "aws-global" as the Region.
 *
 * Starting with the AWS SDK for C++ version 1.8, you can make requests to
 * Amazon S3 across AWS Regions by specifying aws-global as the AWS Region
 * during S3 API client configuration. In this example, an S3 API client
 * set to the aws-global AWS Region is able to list objects in an S3 bucket
 * that is located in the us-west-2 AWS Region.

 */

//! Helper routine to create a unique S3 bucket in us-west-2 Region.
/*!
  \param s3Client: An Aws S3 client.
  \return String: The name of the created bucket.
*/
static const int MAX_TIMEOUT_RETRIES = 20;

static Aws::String createOneBucket(const Aws::String &bucketNamePrefix, const Aws::S3::S3Client &s3Client) {
    // Create an S3 bucket within the us-west-2 AWS Region.
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    Aws::String bucketName = bucketNamePrefix +
                             Aws::Utils::StringUtils::ToLower(uuid.c_str());

    Aws::S3::Model::CreateBucketRequest createBucketRequest;
    createBucketRequest.SetBucket(bucketName);
    Aws::S3::Model::CreateBucketConfiguration createBucketConfiguration;
    createBucketConfiguration.SetLocationConstraint(
            Aws::S3::Model::BucketLocationConstraint::us_west_2);
    createBucketRequest.SetCreateBucketConfiguration(createBucketConfiguration);
    auto createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

    if (createBucketOutcome.IsSuccess()) {
        std::cout << "Success. Created the bucket named '" << bucketName <<
                  "'." << std::endl;
    } else {
        std::cerr << "Error. Could not create the bucket: " <<
                  createBucketOutcome.GetError() << std::endl;

        return "";
    }

    // Wait for the bucket to propagate before continuing.
    unsigned timeoutCount = 0;
    while (timeoutCount++ < MAX_TIMEOUT_RETRIES) {
        Aws::S3::Model::HeadBucketRequest headBucketRequest;
        headBucketRequest.SetBucket(bucketName);
        Aws::S3::Model::HeadBucketOutcome headBucketOutcome = s3Client.HeadBucket(
                headBucketRequest);
        if (headBucketOutcome.IsSuccess()) {
            break;
        }

        std::this_thread::sleep_for(std::chrono::seconds(10));
    }

    return bucketName;
}

//! Helper routine to list objects in a bucket using aws-global.
/*!
  \param s3Client: An S3 client.
  \param bucketName: An S3 bucket name.
  \return bool: Function succeeded.
*/

static bool
listTheObjects(const Aws::S3::S3Client &s3Client, const Aws::String &bucketName) {
    // An S3 API client set to the aws-global AWS Region should be able to get 
    // access to a bucket in any AWS Region.

    Aws::S3::Model::ListObjectsV2Request listObjectsRequest;
    listObjectsRequest.SetBucket(bucketName);

    Aws::String continuationToken;  // Used for pagination.
    Aws::Vector<Aws::S3::Model::Object> objects;

    do {
        if (!continuationToken.empty()) {
            listObjectsRequest.SetContinuationToken(continuationToken);
        }

        // List the objects in the bucket.
        auto listObjectOutcome = s3Client.ListObjectsV2(listObjectsRequest);

        if (listObjectOutcome.IsSuccess()) {
            auto &contents = listObjectOutcome.GetResult().GetContents();

            objects.insert(objects.end(), contents.begin(), contents.end());
            continuationToken = listObjectOutcome.GetResult().GetNextContinuationToken();
        } else {

            std::cerr << "Error. Could not count the objects in the bucket: " <<
                      listObjectOutcome.GetError() << std::endl;
            return false;
        }

    } while (!continuationToken.empty());

    std::cout << "Success. Found " << objects.size() << " objects in the bucket." <<
              std::endl;

    return true;
}
//! Helper routine to delete a bucket.
/*!
  \param s3Client: An Aws S3 client.
  \param bucketName: A bucket to delete.
  \return bool: Function succeeded.
*/

bool deleteABucket(const Aws::S3::S3Client &s3Client, const Aws::String &bucketName) {
    Aws::S3::Model::DeleteBucketRequest deleteBucketRequest;
    deleteBucketRequest.SetBucket(bucketName);
    auto deleteBucketOutcome = s3Client.DeleteBucket(deleteBucketRequest);

    if (deleteBucketOutcome.IsSuccess()) {
        std::cout << "Success. Deleted the bucket named '" << bucketName <<
                  "'." << std::endl;
    } else {
        std::cerr << "Error. Could not delete the bucket: " <<
                  deleteBucketOutcome.GetError() << std::endl;
        std::cerr << "To clean up, you must delete the bucket named '" <<
                  bucketName << "' yourself." << std::endl;
    }

    return deleteBucketOutcome.IsSuccess();
}

//! Routine which demonstrates listing the objects in a bucket using aws-global.
/*!
 \param clientConfig: Aws client configuration.
 \return bool: Function succeeded.
*/

bool AwsDoc::S3::listObjectsWithAwsGlobalRegion(
        const Aws::String &bucketNamePrefix,
        const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3ClientConfiguration config(clientConfig);
    config.region = Aws::Region::AWS_GLOBAL;

    Aws::S3::S3Client s3Client(config);

    Aws::String bucketName = createOneBucket(bucketNamePrefix, s3Client);
    if (bucketName.empty()) {
        return false;
    }

    if (!listTheObjects(s3Client, bucketName)) {
        return false;
    }

    if (!deleteABucket(s3Client, bucketName)) {
        return false;
    }

    return true;
}

/**
 *
 * main function
 *
 * Usage: ' run_list_objects_with_aws_global_region_bucket <bucket_name_prefix>'
 *
 */

#ifndef EXCLUDE_MAIN_FUNCTION

int main(int argc, char *argv[]) {
    if (argc != 2) {
        std::cout << R"(
Usage:
    run_list_objects_with_aws_global_region_bucket <bucket_name_prefix>
Where:
    bucket_name - A bucket name prefix which will be made unique by appending a UUID.
)" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;

    InitAPI(options);

    Aws::String bucketNamePrefix = argv[1];
    {
        Aws::S3::S3ClientConfiguration config;
        AwsDoc::S3::listObjectsWithAwsGlobalRegion(bucketNamePrefix, config);
    }
    ShutdownAPI(options);

    return 0;
}

#endif  // EXCLUDE_MAIN_FUNCTION
