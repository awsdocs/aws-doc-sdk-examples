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
#include <aws/iam/model/ListPoliciesRequest.h>
#include <aws/iam/model/ListPoliciesResult.h>

#include <iostream>

static const char* SIMPLE_DATE_FORMAT_STR = "%Y-%m-%d";

/**
 * Lists all iam policies
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::IAM::IAMClient iam_client;

    Aws::IAM::Model::ListPoliciesRequest listPoliciesRequest;

    bool done = false;
    bool header = false;
    while(!done)
    {
        auto listPoliciesOutcome = iam_client.ListPolicies(listPoliciesRequest);
        if(!listPoliciesOutcome.IsSuccess())
        {
            std::cout << "Failed to list iam policies: " << listPoliciesOutcome.GetError().GetMessage() << std::endl;
            break;
        }

        if(!header)
        {
            std::cout << std::left << std::setw(55) << "Name" 
                                   << std::setw(30) << "ID" 
                                   << std::setw(80) << "Arn"
                                   << std::setw(64) << "Description"
                                   << std::setw(12) << "CreateDate" << std::endl;
            header = true;
        }

        const auto& policies = listPoliciesOutcome.GetResult().GetPolicies();
        for (const auto& policy : policies)
        {
            std::cout << std::left << std::setw(55) << policy.GetPolicyName() 
                                   << std::setw(30) << policy.GetPolicyId()
                                   << std::setw(80) << policy.GetArn()
                                   << std::setw(64) << policy.GetDescription()
                                   << std::setw(12) << policy.GetCreateDate().ToGmtString(SIMPLE_DATE_FORMAT_STR) << std::endl;
        }

        if(listPoliciesOutcome.GetResult().GetIsTruncated())
        {
            listPoliciesRequest.SetMarker(listPoliciesOutcome.GetResult().GetMarker());
        }
        else
        {
            done = true;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



