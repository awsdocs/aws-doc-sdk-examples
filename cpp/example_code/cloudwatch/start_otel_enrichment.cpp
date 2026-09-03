// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: start_otel_enrichment.cpp demonstrates how to turn on OpenTelemetry
 * enrichment for an account so that Amazon CloudWatch vended metrics become queryable
 * with PromQL.
 *
 * Once enrichment is running, CloudWatch vended metrics that carry a resource identifier
 * dimension, such as the Amazon EC2 CPUUtilization metric with its InstanceId dimension,
 * are decorated with resource ARN and resource tag labels.
 *
 * Prerequisites:
 * Resource tags on telemetry must already be enabled for the account.
 *
 * Note that OTLP metric ingestion is not an AWS SDK operation. To send OpenTelemetry
 * metrics to CloudWatch, point an OpenTelemetry collector or the AWS Distro for
 * OpenTelemetry (ADOT) SDK at the CloudWatch OTLP metrics endpoint,
 * https://monitoring.<region>.amazonaws.com/v1/metrics.
 *
 * Inputs:
 * None.
 *
 * Output:
 * OTel enrichment is started for the account.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.start_otel_enrichment.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/StartOTelEnrichmentRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.start_otel_enrichment.inc]

int main(int argc, char **argv) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[cw.cpp.start_otel_enrichment.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::StartOTelEnrichmentRequest request;

        auto outcome = cw.StartOTelEnrichment(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to start OTel enrichment: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            std::cout << "Successfully started OTel enrichment for this account."
                      << std::endl;
        }
        // snippet-end:[cw.cpp.start_otel_enrichment.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
