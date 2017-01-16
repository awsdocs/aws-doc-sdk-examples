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

#include <aws/events/CloudWatchEventsClient.h>
#include <aws/events/model/PutRuleRequest.h>
#include <aws/events/model/PutRuleResult.h>

#include <aws/core/utils/Outcome.h>

#include <iostream>

/**
 * Creates a cloud watch event-routing rule, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: cwe_put_rule <rule_name> <role_arn>" << std::endl;
        return 1;
    }

    Aws::String ruleName(argv[1]);
    Aws::String roleArn(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::CloudWatchEvents::CloudWatchEventsClient cwe_client;

    Aws::CloudWatchEvents::Model::PutRuleRequest putRuleRequest;
    putRuleRequest.SetName(ruleName);
    putRuleRequest.SetRoleArn(roleArn);
    putRuleRequest.SetScheduleExpression("rate(5 minutes)");
    putRuleRequest.SetState(Aws::CloudWatchEvents::Model::RuleState::ENABLED);

    auto putRuleOutcome = cwe_client.PutRule(putRuleRequest);
    if(!putRuleOutcome.IsSuccess())
    {
        std::cout << "Failed to create cloudwatch events rule " << ruleName << ": " << putRuleOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully created cloudwatch events rule " << ruleName << " with resulting Arn " << putRuleOutcome.GetResult().GetRuleArn() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



