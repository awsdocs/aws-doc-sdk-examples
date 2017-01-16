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
#include <aws/events/model/PutTargetsRequest.h>
#include <aws/events/model/PutTargetsResult.h>

#include <aws/core/utils/Outcome.h>

#include <iostream>

/**
 * Creates a cloud watch event routing rule target, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage: cwe_put_targets <rule_name> <lambda_function_arn> <target_id>" << std::endl;
        return 1;
    }

    Aws::String ruleName(argv[1]);
    Aws::String lambdaArn(argv[2]);
    Aws::String targetId(argv[3]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::CloudWatchEvents::CloudWatchEventsClient cwe_client;

    Aws::CloudWatchEvents::Model::Target target;
    target.SetArn(lambdaArn);
    target.SetId(targetId);

    Aws::CloudWatchEvents::Model::PutTargetsRequest putTargetsRequest;
    putTargetsRequest.SetRule(ruleName);
    putTargetsRequest.AddTargets(target);

    auto putTargetsOutcome = cwe_client.PutTargets(putTargetsRequest);
    if (!putTargetsOutcome.IsSuccess())
    {
        std::cout << "Failed to create cloudwatch events target for rule " << ruleName << ": " << putTargetsOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully created cloudwatch events target for rule " << ruleName << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



