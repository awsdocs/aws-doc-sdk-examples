// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: describe_alarm_contributors.cpp demonstrates how to list the contributors for
 * an Amazon CloudWatch PromQL alarm.
 *
 * Each contributor is one series that the alarm's query matched, identified by its label
 * set. This is how you find out which hosts, services, or pods are breaching, rather
 * than only that something is.
 *
 * Prerequisites:
 * A CloudWatch PromQL alarm.
 *
 * Inputs:
 * - alarm_name: The name of the PromQL alarm (entered as the first argument in the
 *   command line).
 *
 * Output:
 * The contributors are listed with their labels and state reasons.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.describe_alarm_contributors.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/DescribeAlarmContributorsRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.describe_alarm_contributors.inc]

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage:" << "  run_describe_alarm_contributors <alarm_name>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alarm_name(argv[1]);

        // snippet-start:[cw.cpp.describe_alarm_contributors.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::DescribeAlarmContributorsRequest request;
        request.SetAlarmName(alarm_name);

        bool done = false;
        bool header = false;
        while (!done) {
            auto outcome = cw.DescribeAlarmContributors(request);
            if (!outcome.IsSuccess()) {
                std::cerr << "Failed to describe alarm contributors: "
                          << outcome.GetError().GetMessage() << std::endl;
                break;
            }

            const auto &contributors = outcome.GetResult().GetAlarmContributors();
            if (!header) {
                if (contributors.empty()) {
                    std::cout << "No contributors yet. The query matched no series, "
                                 "which usually means no OTel metrics with these labels "
                                 "have arrived."
                              << std::endl;
                    break;
                }
                std::cout << "Contributors for alarm " << alarm_name << ":" << std::endl;
                header = true;
            }

            for (const auto &contributor : contributors) {
                std::cout << "  " << contributor.GetContributorId() << ": ";
                bool first = true;
                for (const auto &label : contributor.GetContributorAttributes()) {
                    if (!first) {
                        std::cout << ", ";
                    }
                    std::cout << label.first << "=" << label.second;
                    first = false;
                }
                std::cout << std::endl;
                std::cout << "    reason: " << contributor.GetStateReason() << std::endl;
            }

            const auto &next_token = outcome.GetResult().GetNextToken();
            request.SetNextToken(next_token);
            done = next_token.empty();
        }
        // snippet-end:[cw.cpp.describe_alarm_contributors.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
