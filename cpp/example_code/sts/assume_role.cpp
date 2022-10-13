/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrate assuming an IAM role defined on an external account.
 *
 */

#include <aws/s3/S3Client.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/AssumeRoleRequest.h>
#include <aws/core/auth/AWSCredentialsProvider.h>
#include <aws/s3/model/ListBucketsResult.h>
#include <iostream>
#include "sts_samples.h"

//! Assumes an IAM role defined on an external account.
/*!
  \sa assumeRole()
  \param roleArn: The role Amazon Resource Name (ARN).
  \param roleSessionName: A role session name.
  \param externalId: An external identifier.
  \param credentials: AWSCredentials instance to receive role credentials.
  \param clientConfig Aws client configuration.
  \return bool: Successful completion.
*/
// snippet-start:[sts.cpp.assume_role]
bool AwsDoc::STS::assumeRole(const Aws::String &roleArn,
                             const Aws::String &roleSessionName,
                             const Aws::String &externalId,
                             Aws::Auth::AWSCredentials &credentials,
                             const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::STS::STSClient sts(clientConfig);
    Aws::STS::Model::AssumeRoleRequest sts_req;

    sts_req.SetRoleArn(roleArn);
    sts_req.SetRoleSessionName(roleSessionName);
    sts_req.SetExternalId(externalId);

    const Aws::STS::Model::AssumeRoleOutcome outcome = sts.AssumeRole(sts_req);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error assuming IAM role. " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Credentials successfully retrieved." << std::endl;
        const Aws::STS::Model::AssumeRoleResult result = outcome.GetResult();
        const Aws::STS::Model::Credentials &temp_credentials = result.GetCredentials();

        // Store temporary credentials in return argument
        // Note: The credentials object returned by assumeRole differs
        // from the AWSCredentials object used in most situations.
        credentials.SetAWSAccessKeyId(temp_credentials.GetAccessKeyId());
        credentials.SetAWSSecretKey(temp_credentials.GetSecretAccessKey());
        credentials.SetSessionToken(temp_credentials.GetSessionToken());
    }

    return outcome.IsSuccess();
}
// snippet-end:[sts.cpp.assume_role]

/*
 *
 *  main function
 *
 * Prerequisites: An existing IAM role.
 *
 * Usage: 'run_assume_role <role_arn> <role_session_name>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv)
{
    if (argc != 3) {
        std::cout << "run_assume_role <role_arn> <role_session_name>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String roleArn = argv[1];
        Aws::String roleSessionName = argv[2];
        Aws::String externalId = "012345";	// Optional, but recommended
        Aws::Auth::AWSCredentials credentials;

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        // Assume the role on the external account
        if (!AwsDoc::STS::assumeRole(roleArn, roleSessionName, externalId, credentials, clientConfig))
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

#endif // TESTING_BUILD
