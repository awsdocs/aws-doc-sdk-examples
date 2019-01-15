 
//snippet-sourcedescription:[delete_policy.cpp demonstrates how to delete a managed policy from an AWS account.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
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
//snippet-start:[iam.cpp.delete_policy.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DeletePolicyRequest.h>
#include <iostream>
//snippet-end:[iam.cpp.delete_policy.inc]

/**
 * Deletes an IAM policy based on command line input; only works for policies
 * that have not been associated with other resources To delete a policy in the
 * non-trivial case, use the DeletePolicy operation within the
 * aws-cpp-sdk-access-management high level sdk
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_policy <policy_arn>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String policy_arn(argv[1]);

        // snippet-start:[iam.cpp.delete_policy.code]
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::DeletePolicyRequest request;
        request.SetPolicyArn(policy_arn);

        auto outcome = iam.DeletePolicy(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error deleting policy with arn " << policy_arn << ": "
                << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted policy with arn " << policy_arn
                << std::endl;
        }
        // snippet-end:[iam.cpp.delete_policy.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

