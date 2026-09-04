// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_promql_metric_alarm.cpp demonstrates how to create an Amazon CloudWatch
 * alarm that evaluates a PromQL query against OpenTelemetry metrics.
 *
 * A PromQL alarm differs from a classic metric alarm in a few ways. The query can match
 * many series at once, and each matching series is tracked separately as a contributor.
 * Instead of counting breaching periods, you specify durations: a contributor moves to
 * ALARM after it breaches continuously for the pending period, and back to OK after it
 * stops breaching for the recovery period. A PromQL alarm starts in the OK state rather
 * than INSUFFICIENT_DATA.
 *
 * EvaluationCriteria is mutually exclusive with the classic MetricName and Metrics
 * fields. When you use it you must also set EvaluationInterval, and you must not set
 * Period, Statistic, Threshold, ComparisonOperator, EvaluationPeriods,
 * DatapointsToAlarm, or TreatMissingData.
 *
 * Prerequisites:
 * OpenTelemetry metrics flowing into the account, and OTel enrichment started if the
 * query selects on enriched labels.
 *
 * Inputs:
 * - alarm_name: The name of the alarm (entered as the first argument in the command
 *   line).
 * - query: The PromQL query to evaluate (entered as the second argument in the command
 *   line). The comparison belongs in the query itself; there is no separate threshold.
 *
 * Output:
 * The PromQL alarm is created.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.put_promql_metric_alarm.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/AlarmPromQLCriteria.h>
#include <aws/monitoring/model/EvaluationCriteria.h>
#include <aws/monitoring/model/PutMetricAlarmRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.put_promql_metric_alarm.inc]

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage:" << "  run_put_promql_metric_alarm "
                  << "<alarm_name> <query>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alarm_name(argv[1]);
        Aws::String query(argv[2]);

        // snippet-start:[cw.cpp.put_promql_metric_alarm.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::AlarmPromQLCriteria promQLCriteria;
        promQLCriteria.SetQuery(query);
        // A contributor moves to ALARM after breaching continuously for 300 seconds,
        // and back to OK after 120 seconds without breaching.
        promQLCriteria.SetPendingPeriod(300);
        promQLCriteria.SetRecoveryPeriod(120);

        Aws::CloudWatch::Model::EvaluationCriteria evaluationCriteria;
        evaluationCriteria.SetPromQLCriteria(promQLCriteria);

        Aws::CloudWatch::Model::PutMetricAlarmRequest request;
        request.SetAlarmName(alarm_name);
        request.SetAlarmDescription("A PromQL alarm created by the AWS SDK for C++.");
        request.SetEvaluationCriteria(evaluationCriteria);
        // Valid values are 10, 20, 30, and any multiple of 60, up to 3600.
        request.SetEvaluationInterval(30);

        auto outcome = cw.PutMetricAlarm(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to create PromQL alarm: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            std::cout << "Successfully created PromQL alarm " << alarm_name
                      << " for query " << query << std::endl;
        }
        // snippet-end:[cw.cpp.put_promql_metric_alarm.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
