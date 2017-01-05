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

Aws::String BuildSamplePolicyDocument(const Aws::String& resourceArn)
{
    char policyBuffer[512];
    sprintf_s(policyBuffer, POLICY_TEMPLATE, resourceArn.c_str(), resourceArn.c_str());

    return Aws::String(policyBuffer);
}

/**
 * Creates a fixed policy with name based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: iam_create_policy <policy_name> <resource_arn>" << std::endl;
        return 1;
    }

    Aws::String policyName(argv[1]);
    Aws::String resourceArn(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::IAM::IAMClient iamClient;

    Aws::IAM::Model::CreatePolicyRequest createPolicyRequest;
    createPolicyRequest.SetPolicyName(policyName);
    createPolicyRequest.SetPolicyDocument(BuildSamplePolicyDocument(resourceArn));

    auto createPolicyOutcome = iamClient.CreatePolicy(createPolicyRequest);
    if(!createPolicyOutcome.IsSuccess())
    {
        std::cout << "Error creating policy " << policyName << ": " << createPolicyOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully created policy " << policyName << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



