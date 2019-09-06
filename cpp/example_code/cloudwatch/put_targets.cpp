 
//snippet-sourcedescription:[put_targets.cpp demonstrates how to create an Amazon CloudWatch Events routing rule target.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon CloudWatch Events]
//snippet-service:[events]
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
//snippet-start:[cw.cpp.put_targets.inc]
#include <aws/core/Aws.h>
#include <aws/events/CloudWatchEventsClient.h>
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
        Aws::CloudWatchEvents::CloudWatchEventsClient cwe;

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



