// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: get_otel_enrichment.cpp demonstrates how to get the current OpenTelemetry
 * enrichment status for an account.
 *
 * Inputs:
 * None.
 *
 * Output:
 * The enrichment status, either Running or Stopped.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.get_otel_enrichment.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/GetOTelEnrichmentRequest.h>
#include <aws/monitoring/model/OTelEnrichmentStatus.h>
#include <iostream>
// snippet-end:[cw.cpp.get_otel_enrichment.inc]

int main(int argc, char **argv) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[cw.cpp.get_otel_enrichment.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::GetOTelEnrichmentRequest request;

        auto outcome = cw.GetOTelEnrichment(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to get OTel enrichment status: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            auto status = outcome.GetResult().GetStatus();
            std::cout << "OTel enrichment status is "
                      << Aws::CloudWatch::Model::OTelEnrichmentStatusMapper::
                             GetNameForOTelEnrichmentStatus(status)
                      << "." << std::endl;

            if (status == Aws::CloudWatch::Model::OTelEnrichmentStatus::Running) {
                std::cout << "Vended metrics are queryable with PromQL." << std::endl;
            } else {
                std::cout << "Start enrichment to enrich vended metrics with resource "
                             "ARN and tag labels."
                          << std::endl;
            }
        }
        // snippet-end:[cw.cpp.get_otel_enrichment.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
