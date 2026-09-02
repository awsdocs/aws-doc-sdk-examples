// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: get_alarm_mute_rule.cpp demonstrates how to get the configuration of an
 * Amazon CloudWatch alarm mute rule, including its schedule, the alarms it targets, and
 * whether it is currently SCHEDULED, ACTIVE, or EXPIRED.
 *
 * Prerequisites:
 * A CloudWatch alarm mute rule.
 *
 * Inputs:
 * - mute_rule_name: The name of the mute rule (entered as the first argument in the
 *   command line).
 *
 * Output:
 * The mute rule configuration is printed.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.get_alarm_mute_rule.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/AlarmMuteRuleStatus.h>
#include <aws/monitoring/model/GetAlarmMuteRuleRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.get_alarm_mute_rule.inc]

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage:" << "  run_get_alarm_mute_rule <mute_rule_name>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String mute_rule_name(argv[1]);

        // snippet-start:[cw.cpp.get_alarm_mute_rule.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::GetAlarmMuteRuleRequest request;
        request.SetAlarmMuteRuleName(mute_rule_name);

        auto outcome = cw.GetAlarmMuteRule(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to get alarm mute rule: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            const auto &result = outcome.GetResult();
            std::cout << "Mute rule " << result.GetName() << " is "
                      << Aws::CloudWatch::Model::AlarmMuteRuleStatusMapper::
                             GetNameForAlarmMuteRuleStatus(result.GetStatus())
                      << "." << std::endl;
            std::cout << "  ARN: " << result.GetAlarmMuteRuleArn() << std::endl;
            std::cout << "  schedule: " << result.GetRule().GetSchedule().GetExpression()
                      << " for " << result.GetRule().GetSchedule().GetDuration()
                      << std::endl;

            const auto &alarm_names = result.GetMuteTargets().GetAlarmNames();
            if (!alarm_names.empty()) {
                std::cout << "  muted alarms:";
                for (const auto &alarm_name : alarm_names) {
                    std::cout << " " << alarm_name;
                }
                std::cout << std::endl;
            }
        }
        // snippet-end:[cw.cpp.get_alarm_mute_rule.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
