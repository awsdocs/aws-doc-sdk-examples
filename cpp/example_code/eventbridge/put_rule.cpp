// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_rule.cpp demonstrates how to create an Amazon CloudWatch Events routing rule.
 *
 * Inputs:
 * - rule_name: The name of the rule.
 * - role_arn: The Amazon Resource Name (ARN) of the IAM role associated with the rule.
 *
 * Outputs:
 * The routing rule is created.
   * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cw.cpp.put_rule.inc]
#include <aws/core/Aws.h>
#include <aws/events/EventBridgeClient.h>
#include <aws/events/model/PutRuleRequest.h>
#include <aws/events/model/PutRuleResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>
//snippet-end:[cw.cpp.put_rule.inc]

/**
 * Creates a cloud watch event-routing rule, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: put_rule <rule_name> <role_arn>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String rule_name(argv[1]);
        Aws::String role_arn(argv[2]);

        // snippet-start:[cw.cpp.put_rule.code]
        Aws::CloudWatchEvents::EventBridgeClient cwe;
        Aws::CloudWatchEvents::Model::PutRuleRequest request;
        request.SetName(rule_name);
        request.SetRoleArn(role_arn);
        request.SetScheduleExpression("rate(5 minutes)");
        request.SetState(Aws::CloudWatchEvents::Model::RuleState::ENABLED);

        auto outcome = cwe.PutRule(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to create CloudWatch events rule " <<
                rule_name << ": " << outcome.GetError().GetMessage() <<
                std::endl;
        }
        else
        {
            std::cout << "Successfully created CloudWatch events rule " <<
                rule_name << " with resulting Arn " <<
                outcome.GetResult().GetRuleArn() << std::endl;
        }
        // snippet-end:[cw.cpp.put_rule.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

