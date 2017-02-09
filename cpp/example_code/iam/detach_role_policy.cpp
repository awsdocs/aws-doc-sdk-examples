/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DetachRolePolicyRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesResult.h>

#include <iostream>

void DetachRolePolicy(const Aws::String& roleName, const Aws::String& policyArn)
{
    Aws::IAM::IAMClient iam_client;

    Aws::IAM::Model::ListAttachedRolePoliciesRequest listAttachedRolePoliciesRequest;
    listAttachedRolePoliciesRequest.SetRoleName(roleName);

    bool done = false;
    bool attached = false;
    while(!done)
    {
        auto listAttachedRolePoliciesOutcome = iam_client.ListAttachedRolePolicies(listAttachedRolePoliciesRequest);
        if(!listAttachedRolePoliciesOutcome.IsSuccess())
        {
            std::cout << "Failed to list attached policies of role " << roleName << ": " << listAttachedRolePoliciesOutcome.GetError().GetMessage() << std::endl;
            return;
        }

        const auto& policies = listAttachedRolePoliciesOutcome.GetResult().GetAttachedPolicies();
        attached = std::any_of(policies.cbegin(), policies.cend(), [=](const Aws::IAM::Model::AttachedPolicy& policy){ return policy.GetPolicyArn() == policyArn; });
        if(attached)
        {
            break;
        }

        done = !listAttachedRolePoliciesOutcome.GetResult().GetIsTruncated();
        listAttachedRolePoliciesRequest.SetMarker(listAttachedRolePoliciesOutcome.GetResult().GetMarker());
    }

    if(!attached)
    {
        std::cout << "Policy " << policyArn << " is not attached to role " << roleName << std::endl;
        return;
    }

    Aws::IAM::Model::DetachRolePolicyRequest detachRolePolicyRequest;
    detachRolePolicyRequest.SetRoleName(roleName);
    detachRolePolicyRequest.SetPolicyArn(policyArn);

    auto detachRolePolicyOutcome = iam_client.DetachRolePolicy(detachRolePolicyRequest);
    if(!detachRolePolicyOutcome.IsSuccess())
    {
        std::cout << "Failed to detach policy " << policyArn << " from role " << roleName << ": " << detachRolePolicyOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully detached policy " << policyArn << " from role " << roleName << std::endl;
}

static const char* SAMPLE_POLICY_ARN = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess";

/**
 * Detaches a policy from a role, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: iam_detach_role_policy <role_name> <policy_arn>" << std::endl;
        return 1;
    }

    Aws::String roleName(argv[1]);
    Aws::String policyArn = argv[2];

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    DetachRolePolicy(roleName, policyArn);

    Aws::ShutdownAPI(options);

    return 0;
}



