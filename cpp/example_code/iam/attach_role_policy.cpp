/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates attaching a policy to a role.
 *
 */

//snippet-start:[iam.cpp.attach_role_policy.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/AttachRolePolicyRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesResult.h>
#include <iostream>
#include <iomanip>
#include "iam_samples.h"
//snippet-end:[iam.cpp.attach_role_policy.inc]

// snippet-start:[iam.cpp.attach_role_policy.code]
//! Demonstrates attaching a policy to a role.
/*!
  \sa attachRolePolicy()
  \param roleName: The name of the role.
  \param policyArn The policy Amazon Resource Name (ARN) to attach.
  \param clientConfig Aws client configuration.
  \return bool: Successful completion.
*/
bool AwsDoc::IAM::attachRolePolicy(const Aws::String &roleName,
                                   const Aws::String &policyArn,
                                   const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iam(clientConfig);

    Aws::IAM::Model::ListAttachedRolePoliciesRequest list_request;
    list_request.SetRoleName(roleName);

    bool done = false;
    while (!done) {
        auto list_outcome = iam.ListAttachedRolePolicies(list_request);
        if (!list_outcome.IsSuccess()) {
            std::cerr << "Failed to list attached policies of role " <<
                      roleName << ": " << list_outcome.GetError().GetMessage() <<
                      std::endl;
            return false;
        }

        const auto &policies = list_outcome.GetResult().GetAttachedPolicies();
        if (std::any_of(policies.cbegin(), policies.cend(),
                        [=](const Aws::IAM::Model::AttachedPolicy &policy) {
                                return policy.GetPolicyArn() == policyArn;
                        })) {
            std::cout << "Policy " << policyArn <<
                      " is already attached to role " << roleName << std::endl;
            return true;
        }

        done = !list_outcome.GetResult().GetIsTruncated();
        list_request.SetMarker(list_outcome.GetResult().GetMarker());
    }

    Aws::IAM::Model::AttachRolePolicyRequest request;
    request.SetRoleName(roleName);
    request.SetPolicyArn(policyArn);

    auto outcome = iam.AttachRolePolicy(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to attach policy " << policyArn << " to role " <<
                  roleName << ": " << outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully attached policy " << policyArn << " to role " <<
                  roleName << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.attach_role_policy.code]

static const char *SAMPLE_POLICY_ARN =
        "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess";

/*
 *
 *  main function
 *
 * Prerequisites: An existing IAM role.
 *
 * Usage: 'run_attach_role_policy <role_name> [policy_arn]'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 2 || argc >= 4) {
        std::cout << "Usage: run_attach_role_policy <role_name> [policy_arn]" <<
                  std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String role_name(argv[1]);
        Aws::String policy_arn;
        if (argc == 3) {
            policy_arn = argv[2];
        }
        else {
            policy_arn = SAMPLE_POLICY_ARN;
        }

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::attachRolePolicy(role_name, policy_arn, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD