// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_alarm_mute_rule.cpp demonstrates how to create or update an Amazon
 * CloudWatch alarm mute rule.
 *
 * While a mute rule is active the targeted alarms keep evaluating and keep transitioning
 * between states, but their configured actions do not fire. This is the supported way to
 * suppress notifications during a known maintenance window, instead of disabling alarm
 * actions and relying on someone to turn them back on.
 *
 * Inputs:
 * - mute_rule_name: The name of the mute rule (entered as the first argument in the
 *   command line).
 * - alarm_name: The name of the alarm to mute (entered as the second argument in the
 *   command line).
 *
 * Output:
 * The alarm mute rule is created or updated.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.put_alarm_mute_rule.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/MuteTargets.h>
#include <aws/monitoring/model/PutAlarmMuteRuleRequest.h>
#include <aws/monitoring/model/Rule.h>
#include <aws/monitoring/model/Schedule.h>
#include <iostream>
// snippet-end:[cw.cpp.put_alarm_mute_rule.inc]

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage:" << "  run_put_alarm_mute_rule "
                  << "<mute_rule_name> <alarm_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String mute_rule_name(argv[1]);
        Aws::String alarm_name(argv[2]);

        // snippet-start:[cw.cpp.put_alarm_mute_rule.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        // For a recurring window, use a five-field cron expression,
        // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five
        // fields, not the six that Amazon EventBridge uses. For a one-time window, use
        // an at expression such as at(2026-09-05T02:00).
        Aws::CloudWatch::Model::Schedule schedule;
        schedule.SetExpression("cron(0 2 * * SUN)");
        // The duration is in ISO 8601 duration format, from PT1M (one minute) to
        // P15D (15 days).
        schedule.SetDuration("PT2H");
        schedule.SetTimezone("America/Los_Angeles");

        Aws::CloudWatch::Model::Rule rule;
        rule.SetSchedule(schedule);

        // Target up to 100 alarms. If MuteTargets is not set, the rule applies to every
        // alarm in the account.
        Aws::CloudWatch::Model::MuteTargets muteTargets;
        muteTargets.AddAlarmNames(alarm_name);

        Aws::CloudWatch::Model::PutAlarmMuteRuleRequest request;
        request.SetName(mute_rule_name);
        request.SetDescription("A mute rule created by the AWS SDK for C++.");
        request.SetRule(rule);
        request.SetMuteTargets(muteTargets);

        auto outcome = cw.PutAlarmMuteRule(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to put alarm mute rule: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            std::cout << "Successfully put alarm mute rule " << mute_rule_name
                      << std::endl;
        }
        // snippet-end:[cw.cpp.put_alarm_mute_rule.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
