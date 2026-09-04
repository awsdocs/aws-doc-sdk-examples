// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: stop_otel_enrichment.cpp demonstrates how to turn off OpenTelemetry
 * enrichment for an account.
 *
 * Existing PromQL alarms are not deleted, but vended metrics stop being enriched with
 * resource ARN and tag labels, so queries that select on those labels stop matching.
 *
 * Inputs:
 * None.
 *
 * Output:
 * OTel enrichment is stopped for the account.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[cw.cpp.stop_otel_enrichment.inc]
#include <aws/core/Aws.h>
#include <aws/monitoring/CloudWatchClient.h>
#include <aws/monitoring/model/StopOTelEnrichmentRequest.h>
#include <iostream>
// snippet-end:[cw.cpp.stop_otel_enrichment.inc]

int main(int argc, char **argv) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[cw.cpp.stop_otel_enrichment.code]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        Aws::CloudWatch::CloudWatchClient cw(clientConfig);

        Aws::CloudWatch::Model::StopOTelEnrichmentRequest request;

        auto outcome = cw.StopOTelEnrichment(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to stop OTel enrichment: "
                      << outcome.GetError().GetMessage() << std::endl;
        } else {
            std::cout << "Successfully stopped OTel enrichment for this account."
                      << std::endl;
        }
        // snippet-end:[cw.cpp.stop_otel_enrichment.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}
