/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html.
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates putting an inline permissions policy on an IAM role.
 *
 */

#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/PutRolePolicyRequest.h>
#include <iostream>
#include "iam_samples.h"

//! Puts an inline permissions policy on an IAM role.
/*!
  \sa putRolePolicy()
  \param roleName: The IAM role name.
  \param policyName: The policy name.
  \param policyDocument: The policy document JSON string.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

bool AwsDoc::IAM::putRolePolicy(
        const Aws::String &roleName,
        const Aws::String &policyName,
        const Aws::String &policyDocument,
        const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iamClient(clientConfig);
    Aws::IAM::Model::PutRolePolicyRequest request;

    request.SetRoleName(roleName);
    request.SetPolicyName(policyName);
    request.SetPolicyDocument(policyDocument);

    Aws::IAM::Model::PutRolePolicyOutcome outcome = iamClient.PutRolePolicy(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error putting policy on role. " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully put the role policy." << std::endl;
    }

    return outcome.IsSuccess();
}

/*
 *
 *  main function
 *
 * Prerequisites: An existing IAM role.
 *
 * Usage: 'run_put_role_policy <roleName> <policyName>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc != 3) {
        std::cout << "Usage: run_put_role_policy <roleName> <policyName>" << std::endl;
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Set these configuration values before running the program
        Aws::String roleName = argv[1];	// An existing IAM role
        Aws::String policyName = argv[2];

        // Define a permissions policy that enables S3 ReadOnly access
        Aws::String permissionsPolicy = R"({
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:Get*",
                        "s3:List*"
                    ],
                    "Resource": "*"
                }
            ]
        })";

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::putRolePolicy(roleName, policyName, permissionsPolicy, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD
