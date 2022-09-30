/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/AssumeRoleRequest.h>
#include <aws/core/auth/AWSCredentialsProvider.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/ListBucketsResult.h>
#include <iostream>
#include "iam_samples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrate assuming an IAM role defined on an external account.
 *
 */

//! Displays the time an access key was last used.
/*!
  \sa accessKeyLastUsed()
  \param secretKeyID: The secret key ID.
  \param clientConfig Aws client configuration.
  \return bool: Successful completion.
*/


/**
 *
 */
Aws::Auth::AWSCredentials * AssumeRole(const Aws::String & roleArn, 
    const Aws::String & roleSessionName, 
    const Aws::String & externalId,
    Aws::Auth::AWSCredentials & credentials)
{
    Aws::STS::STSClient sts;
    Aws::STS::Model::AssumeRoleRequest sts_req;

    sts_req.SetRoleArn(roleArn);
    sts_req.SetRoleSessionName(roleSessionName);
    sts_req.SetExternalId(externalId);

    auto response = sts.AssumeRole(sts_req);

    if (!response.IsSuccess())
    {
        std::cerr << "Error assuming IAM role. " <<
            response.GetError().GetMessage() << std::endl;
        return NULL;
    }

    auto result = response.GetResult();
    auto temp_credentials = result.GetCredentials();

    // Store temporary credentials in return argument
    // Note: The credentials object returned by AssumeRole differs
    // from the AWSCredentials object used in most situations.
    credentials.SetAWSAccessKeyId(temp_credentials.GetAccessKeyId());
    credentials.SetAWSSecretKey(temp_credentials.GetSecretAccessKey());
    credentials.SetSessionToken(temp_credentials.GetSessionToken());
    return &credentials;
}

/**
 * Exercise AssumeRole()
 */
int main(int argc, char **argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Set these configuration values before running the program
        Aws::String roleArn = "arn:aws:iam::ACCOUNT_ID:role/ROLE_NAME";
        Aws::String roleSessionName = "AssumeRoleSession1";
        Aws::String externalId = "012345";	// Optional, but recommended
        Aws::Auth::AWSCredentials credentials;

        // Assume the role on the external account
        if (!AssumeRole(roleArn, roleSessionName, externalId, credentials))
        {
            return 1;
        }

        // List the S3 buckets in the external account. Note: The assumed
        // role must grant the appropriate S3 permissions.
        Aws::S3::S3Client s3(credentials);
        auto response_s3 = s3.ListBuckets();

        if (!response_s3.IsSuccess())
        {
            std::cerr << "Error listing S3 buckets in external account. " << 
                response_s3.GetError().GetMessage() << std::endl;
            return 1;
        }

        auto result_s3 = response_s3.GetResult();
        for (auto &bucket : result_s3.GetBuckets())
        {
            std::cout << bucket.GetName() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
