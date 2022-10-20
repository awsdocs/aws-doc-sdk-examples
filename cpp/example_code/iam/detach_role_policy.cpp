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
 * Demonstrates detaching a policy from a role.
 *
 */

//snippet-start:[iam.cpp.detach_role_policy.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DetachRolePolicyRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesResult.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.detach_role_policy.inc]

//! Detaches a policy from a role.
/*!
  \sa detachRolePolicy()
  \param roleName: The user name.
  \param policyArn: The policy Amazon Resource Name (ARN).
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

bool AwsDoc::IAM::detachRolePolicy(const Aws::String &roleName,
                                   const Aws::String &policyArn,
                                   const Aws::Client::ClientConfiguration &clientConfig) {
    // snippet-start:[iam.cpp.detach_role_policy01.code]
    Aws::IAM::IAMClient iam(clientConfig);
    // snippet-end:[iam.cpp.detach_role_policy01.code]

    Aws::IAM::Model::ListAttachedRolePoliciesRequest list_request;
    list_request.SetRoleName(roleName);

    bool done = false;
    bool attached = false;
    while (!done) {
        auto listOutcome = iam.ListAttachedRolePolicies(list_request);
        if (!listOutcome.IsSuccess()) {
            std::cerr << "Failed to list attached policies of role " <<
                      roleName << ": " << listOutcome.GetError().GetMessage() <<
                      std::endl;
            return false;
        }

        const auto &policies = listOutcome.GetResult().GetAttachedPolicies();
        attached = std::any_of(
                policies.cbegin(), policies.cend(),
                [=](const Aws::IAM::Model::AttachedPolicy &policy) {
                        return policy.GetPolicyArn() == policyArn;
                });
        if (attached) {
            break;
        }

        done = !listOutcome.GetResult().GetIsTruncated();
        list_request.SetMarker(listOutcome.GetResult().GetMarker());
    }

    if (!attached) {
        std::cerr << "Policy " << policyArn << " is not attached to role " <<
                  roleName << std::endl;
        return false;
    }

    // snippet-start:[iam.cpp.detach_role_policy02.code]
    Aws::IAM::Model::DetachRolePolicyRequest detachRequest;
    detachRequest.SetRoleName(roleName);
    detachRequest.SetPolicyArn(policyArn);

    auto detachOutcome = iam.DetachRolePolicy(detachRequest);
    if (!detachOutcome.IsSuccess()) {
        std::cerr << "Failed to detach policy " << policyArn << " from role "
                  << roleName << ": " << detachOutcome.GetError().GetMessage() <<
                  std::endl;
    }
    else {
        std::cout << "Successfully detached policy " << policyArn << " from role "
                  << roleName << std::endl;
    }

    return detachOutcome.IsSuccess();
    // snippet-end:[iam.cpp.detach_role_policy02.code]
}
/*
 *
 *  main function
 *
 * Prerequisites: An existing IAM role with an attached policy.
 *
 * Usage: 'run_detach_role_policy <role_name> <policy_arn>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_detach_role_policy <role_name> <policy_arn>" <<
                  std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String roleName(argv[1]);
        Aws::String policyArn = argv[2];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::detachRolePolicy(roleName, policyArn, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD

