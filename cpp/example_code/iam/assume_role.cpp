
//snippet-sourcedescription:[assume_role.cpp demonstrates how to use Amazon STS AssumeRole to access resources on an external account.]
//snippet-service:[iam]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-02-05]
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

#include <aws/core/Aws.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/AssumeRoleRequest.h>
#include <aws/sts/model/AssumeRoleResult.h>
#include <aws/sts/model/Credentials.h>
#include <aws/core/auth/AWSCredentialsProvider.h>

#include <aws/s3/S3Client.h>
#include <aws/s3/model/ListBucketsResult.h>
#include <iostream>


/**
 * Assume an IAM role defined on an external account.
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
