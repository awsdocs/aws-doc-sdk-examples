 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Identity and Access Management (IAM)]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/iam/model/CreatePolicyRequest.h>
#include <aws/iam/model/CreatePolicyResult.h>
#include <iostream>

static const char* const POLICY_TEMPLATE =
"{"
"  \"Version\": \"2012-10-17\","
"  \"Statement\": ["
"    {"
"        \"Effect\": \"Allow\","
"        \"Action\": \"logs:CreateLogGroup\","
"        \"Resource\": \"%s\""
"    },"
"    {"
"        \"Effect\": \"Allow\","
"        \"Action\": ["
"            \"dynamodb:DeleteItem\","
"            \"dynamodb:GetItem\","
"            \"dynamodb:PutItem\","
"            \"dynamodb:Scan\","
"            \"dynamodb:UpdateItem\""
"       ],"
"       \"Resource\": \"%s\""
"    }"
"   ]"
"}";

Aws::String BuildSamplePolicyDocument(const Aws::String& rsrc_arn)
{
    char policyBuffer[512];
#ifdef WIN32
    sprintf_s(policyBuffer, POLICY_TEMPLATE, rsrc_arn.c_str(), rsrc_arn.c_str());
#else
    sprintf(policyBuffer, POLICY_TEMPLATE, rsrc_arn.c_str(), rsrc_arn.c_str());
#endif // WIN32
    return Aws::String(policyBuffer);
}

/**
 * Creates a fixed policy with name based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: create_policy <policy_name> <resource_arn>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String policy_name(argv[1]);
        Aws::String rsrc_arn(argv[2]);

        Aws::IAM::IAMClient iam;

        Aws::IAM::Model::CreatePolicyRequest request;
        request.SetPolicyName(policy_name);
        request.SetPolicyDocument(BuildSamplePolicyDocument(rsrc_arn));

        auto outcome = iam.CreatePolicy(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error creating policy " << policy_name << ": " <<
                outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully created policy " << policy_name <<
                std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

