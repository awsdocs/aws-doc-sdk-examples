// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: delete_alarm_mute_rule.cpp demonstrates how to delete an Amazon CloudWatch
 * alarm mute rule. The alarms it targeted resume firing their actions.
 *
 * Prerequisites:
 * A CloudWatch alarm mute rule.
 *
 * Inputs:
 * - mute_rule_name: The name of the mute rule to delete (entered as the first argument
 *   in the command line).
 *
 * Output:
 * The alarm mute rule is deleted.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.delete_alarm_mute_rule.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/DeleteAlarmMuteRuleRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.delete_alarm_mute_rule.inc]

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage:" << "  run_delete_alarm_mute_rule <mute_rule_name>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String mute_rule_name(argv[1]);

        // snippet-start:[cw.cpp.delete_alarm_mute_rule.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::DeleteAlarmMuteRuleRequest request;
        request.SetAlarmMuteRuleName(mute_rule_name);

        auto outcome = cw.DeleteAlarmMuteRule(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to delete alarm mute rule: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            std::cout << "Successfully deleted alarm mute rule " << mute_rule_name
                      << std::endl;
        }
        // snippet-end:[cw.cpp.delete_alarm_mute_rule.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
