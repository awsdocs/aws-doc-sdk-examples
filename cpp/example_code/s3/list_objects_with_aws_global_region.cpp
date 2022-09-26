/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/core/utils/logging/LogLevel.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/HeadBucketRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <iostream>
#include <aws/core/utils/UUID.h>
#include "awsdoc/s3/s3_examples.h"

/**
 * Before running this C++ code example, set up your development environment,
 * including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
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
  \fn CreateOneBucket()
  \param s3Client An Aws S3 client.
*/
static const int MAX_TIMEOUT_RETRIES = 20;

static Aws::String CreateOneBucket(const Aws::S3::S3Client &s3Client) {
    // Create an S3 bucket within the us-west-2 AWS Region.
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    Aws::String bucketName = "doc-example-bucket-" +
                             Aws::Utils::StringUtils::ToLower(uuid.c_str());

    Aws::S3::Model::CreateBucketRequest createBucketRequest;
    createBucketRequest.SetBucket(bucketName);
    Aws::S3::Model::CreateBucketConfiguration createBucketConfiguration;
    createBucketConfiguration.SetLocationConstraint(Aws::S3::Model::BucketLocationConstraint::us_west_2);
    createBucketRequest.SetCreateBucketConfiguration(createBucketConfiguration);
    auto createBucketOutcome = s3Client.CreateBucket(createBucketRequest);

    if (createBucketOutcome.IsSuccess()) {
        std::cout << "Success. Created the bucket named '" << bucketName <<
                  "'." << std::endl;
    }
    else {
        std::cerr << "Error. Could not create the bucket: " <<
                  createBucketOutcome.GetError() << std::endl;

        return "";
    }

    // Wait for the bucket to propagate before continuing.
    unsigned timeoutCount = 0;
    while (timeoutCount++ < MAX_TIMEOUT_RETRIES) {
        Aws::S3::Model::HeadBucketRequest headBucketRequest;
        headBucketRequest.SetBucket(bucketName);
        Aws::S3::Model::HeadBucketOutcome headBucketOutcome = s3Client.HeadBucket(headBucketRequest);
        if (headBucketOutcome.IsSuccess()) {
            break;
        }

        std::this_thread::sleep_for(std::chrono::seconds(10));
    }

    return bucketName;
}

//! Helper routine to list objects in a bucket using aws-global.
/*!
  \fn ListTheObjects()
  \param s3Client An S3 client.
  \param bucketName An S3 bucket name.
*/

static bool ListTheObjects(const Aws::S3::S3Client &s3Client, const Aws::String &bucketName) {
    // An S3 API client set to the aws-global AWS Region should be able to get 
    // access to a bucket in any AWS Region.
    Aws::S3::Model::ListObjectsRequest listObjectsRequest;
    listObjectsRequest.SetBucket(bucketName);
    auto listObjectOutcome = s3Client.ListObjects(listObjectsRequest);

    if (listObjectOutcome.IsSuccess()) {
        std::cout << "Success. Number of objects in the bucket named '" <<
                  bucketName << "' is " <<
                  listObjectOutcome.GetResult().GetContents().size() << "." <<
                  std::endl;
    }
    else {
        std::cerr << "Error. Could not count the objects in the bucket: " <<
                  listObjectOutcome.GetError() << std::endl;
    }

    return listObjectOutcome.IsSuccess();
}
//! Helper routine to delete a bucket.
/*!
  \fn DeleteABucket()
  \param s3Client An Aws S3 client.
  \param bucketName A bucket to delete.
*/

bool DeleteABucket(const Aws::S3::S3Client &s3Client, const Aws::String &bucketName) {
    Aws::S3::Model::DeleteBucketRequest deleteBucketRequest;
    deleteBucketRequest.SetBucket(bucketName);
    auto deleteBucketOutcome = s3Client.DeleteBucket(deleteBucketRequest);

    if (deleteBucketOutcome.IsSuccess()) {
        std::cout << "Success. Deleted the bucket named '" << bucketName <<
                  "'." << std::endl;
    }
    else {
        std::cerr << "Error. Could not delete the bucket: " <<
                  deleteBucketOutcome.GetError() << std::endl;
        std::cerr << "To clean up, you must delete the bucket named '" <<
                  bucketName << "' yourself." << std::endl;
    }

    return deleteBucketOutcome.IsSuccess();
}

//! Routine which demonstrates listing the objects in a bucket using aws-global.
/*!
  \fn ListObjectsWithAWSGlobalRegion()
 \param clientConfig Aws client configuration.
*/

bool AwsDoc::S3::ListObjectsWithAWSGlobalRegion(const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::Client::ClientConfiguration config(clientConfig);
    config.region = Aws::Region::AWS_GLOBAL;

    Aws::S3::S3Client s3Client(config);

    Aws::String bucketName = CreateOneBucket(s3Client);
    if (bucketName.empty()) {
        return false;
    }

    if (!ListTheObjects(s3Client, bucketName)) {
        return false;
    }

    if (!DeleteABucket(s3Client, bucketName)) {
        return false;
    }

    return true;
}

/**
 *
 * main function
 *
 */

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;

    InitAPI(options);
    {
        Aws::Client::ClientConfiguration config;
        AwsDoc::S3::ListObjectsWithAWSGlobalRegion(config);
    }
    ShutdownAPI(options);

    return 0;
}

#endif  // TESTING_BUILD