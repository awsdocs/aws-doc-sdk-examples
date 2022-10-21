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
 * Demonstrates getting an IAM policy's information.
 *
 */

//snippet-start:[iam.cpp.get_policy.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/GetPolicyRequest.h>
#include <aws/iam/model/GetPolicyResult.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.get_policy.inc]

//! Gets an IAM policy's information.
/*!
  \sa getPolicy()
  \param policyArn: The policy Amazon Resource Name (ARN).
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

// snippet-start:[iam.cpp.get_policy.code]
bool AwsDoc::IAM::getPolicy(const Aws::String &policyArn,
                            const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iam(clientConfig);
    Aws::IAM::Model::GetPolicyRequest request;
    request.SetPolicyArn(policyArn);

    auto outcome = iam.GetPolicy(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error getting policy " << policyArn << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        const auto &policy = outcome.GetResult().GetPolicy();
        std::cout << "Name: " << policy.GetPolicyName() << std::endl <<
                  "ID: " << policy.GetPolicyId() << std::endl << "Arn: " <<
                  policy.GetArn() << std::endl << "Description: " <<
                  policy.GetDescription() << std::endl << "CreateDate: " <<
                  policy.GetCreateDate().ToGmtString(Aws::Utils::DateFormat::ISO_8601)
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.get_policy.code]

/*
 *
 *  main function
 *
 * Prerequisites: Existing IAM policy.
 *
 * Usage: 'run_get_policy <policy_arn>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_get_policy <policy_arn>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String policyArn(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::getPolicy(policyArn, clientConfig);

    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD

