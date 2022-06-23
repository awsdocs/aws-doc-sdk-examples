
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_targets.cpp demonstrates how to create an Amazon CloudWatch Events routing rule target.
 *
 * Inputs:
 * - filter_name: The name of the filter.
 * - filter_pattern: The filter pattern.
 * - log_group_name: The name of the log group.
 * - lambda_function_arn: The Amazon Resource Name (ARN) of the AWS Lambda function.
 *
 * Outputs:
 * The routing rule target is created.
   * ///////////////////////////////////////////////////////////////////////// */
//snippet-start:[cw.cpp.put_targets.inc]
#include <aws/core/Aws.h>
#include <aws/events/EventBridgeClient.h>
#include <aws/events/model/PutTargetsRequest.h>
#include <aws/events/model/PutTargetsResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>
//snippet-end:[cw.cpp.put_targets.inc]

/**
 * Creates a cloud watch event routing rule target, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage:" << std::endl << "  put_targets " <<
            "<rule_name> <lambda_function_arn> <target_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String rule_name(argv[1]);
        Aws::String lambda_arn(argv[2]);
        Aws::String target_id(argv[3]);

        // snippet-start:[cw.cpp.put_targets.code]
        Aws::CloudWatchEvents::EventBridgeClient cwe;

        Aws::CloudWatchEvents::Model::Target target;
        target.SetArn(lambda_arn);
        target.SetId(target_id);

        Aws::CloudWatchEvents::Model::PutTargetsRequest request;
        request.SetRule(rule_name);
        request.AddTargets(target);

        auto putTargetsOutcome = cwe.PutTargets(request);
        if (!putTargetsOutcome.IsSuccess())
        {
            std::cout << "Failed to create CloudWatch events target for rule "
                << rule_name << ": " <<
                putTargetsOutcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout <<
                "Successfully created CloudWatch events target for rule "
                << rule_name << std::endl;
        }
        // snippet-end:[cw.cpp.put_targets.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}



