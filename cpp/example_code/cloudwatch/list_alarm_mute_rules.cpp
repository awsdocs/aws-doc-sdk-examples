// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: list_alarm_mute_rules.cpp demonstrates how to list the Amazon CloudWatch
 * alarm mute rules in an account, optionally filtered to the rules that target one
 * alarm.
 *
 * Inputs:
 * - alarm_name: Optional. The name of an alarm (entered as the first argument in the
 *   command line). When omitted, every mute rule in the account is listed.
 *
 * Output:
 * The mute rules are listed with their ARNs and statuses.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.list_alarm_mute_rules.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/AlarmMuteRuleStatus.h>
#include <aws/monitoring/model/ListAlarmMuteRulesRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.list_alarm_mute_rules.inc]

int main(int argc, char **argv) {
    if (argc > 2) {
        std::cout << "Usage:" << "  run_list_alarm_mute_rules [alarm_name]" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[cw.cpp.list_alarm_mute_rules.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::ListAlarmMuteRulesRequest request;
        if (argc == 2) {
            request.SetAlarmName(argv[1]);
        }

        bool done = false;
        while (!done) {
            auto outcome = cw.ListAlarmMuteRules(request);
            if (!outcome.IsSuccess()) {
                std::cerr << "Failed to list alarm mute rules: "
                          << outcome.GetError().GetMessage() << std::endl;
                break;
            }

            for (const auto &summary : outcome.GetResult().GetAlarmMuteRuleSummaries()) {
                std::cout << summary.GetAlarmMuteRuleArn() << " ("
                          << Aws::CloudWatch::Model::AlarmMuteRuleStatusMapper::
                                 GetNameForAlarmMuteRuleStatus(summary.GetStatus())
                          << ")" << std::endl;
            }

            const auto &next_token = outcome.GetResult().GetNextToken();
            request.SetNextToken(next_token);
            done = next_token.empty();
        }
        // snippet-end:[cw.cpp.list_alarm_mute_rules.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
