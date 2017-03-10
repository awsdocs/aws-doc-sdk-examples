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
#include <aws/iam/model/GetPolicyRequest.h>
#include <aws/iam/model/GetPolicyResult.h>

#include <iostream>

/**
 * Gets an IAM policy's information, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2) {
        std::cout << "Usage: iam_get_policy <policy_arn>" << std::endl;
        return 1;
    }

    Aws::String policy_arn(argv[1]);
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::GetPolicyRequest request;
        request.SetPolicyArn(policy_arn);

        auto outcome = iam.GetPolicy(request);
        if (!outcome.IsSuccess()) {
            std::cout << "Error getting policy " << policy_arn << ": " <<
                outcome.GetError().GetMessage() << std::endl;
        } else {
            const auto &policy = outcome.GetResult().GetPolicy();
            std::cout << "Name: " << policy.GetPolicyName() << std::endl <<
                "ID: " << policy.GetPolicyId() << std::endl << "Arn: " <<
                policy.GetArn() << std::endl << "Description: " <<
                policy.GetDescription() << std::endl << "CreateDate: " <<
                policy.GetCreateDate().ToGmtString(Aws::Utils::DateFormat::ISO_8601)
                << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

