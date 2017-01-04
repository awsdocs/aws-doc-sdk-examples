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
#include <aws/iam/model/DeletePolicyRequest.h>

#include <iostream>

/**
 * Deletes an IAM policy based on command line input; only works for policies that have not been associated with other resources
 * To delete a policy in the non-trivial case, use the DeletePolicy operation within the aws-cpp-sdk-access-management high level sdk
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: iam_delete_policy <policy_arn>" << std::endl;
        return 1;
    }

    Aws::String policyArn(argv[1]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::IAM::IAMClient iamClient;

    Aws::IAM::Model::DeletePolicyRequest deletePolicyRequest;
    deletePolicyRequest.SetPolicyArn(policyArn);

    auto deletePolicyOutcome = iamClient.DeletePolicy(deletePolicyRequest);
    if (!deletePolicyOutcome.IsSuccess())
    {
        std::cout << "Error deleting policy with arn " << policyArn << ": " << deletePolicyOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully deleted policy with arn " << policyArn << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



