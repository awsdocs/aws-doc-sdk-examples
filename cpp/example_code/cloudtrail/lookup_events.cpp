// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/LookupEventsRequest.h>
#include <aws/cloudtrail/model/LookupEventsResult.h>
#include <iostream>

/**
 * Look up Cloud Trail events based on command line input
 */

int main(int argc, char **argv) {
    if (argc != 1) {
        std::cout << "Usage: lookup_events";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::CloudTrail::CloudTrailClient ct;

        Aws::CloudTrail::Model::LookupEventsRequest le_req;

        int event_count = 0;
        Aws::String nextToken; // Used for pagination.
        do {
            if (!nextToken.empty()) {
                le_req.SetNextToken(nextToken);
            }

            auto le_out = ct.LookupEvents(le_req);

            if (le_out.IsSuccess()) {
                std::cout << "Successfully looking up cloudtrail events:";
                const auto &events = le_out.GetResult().GetEvents();

                for (auto val: events) {
                    std::cout << " " << val.GetEventName() << std::endl;
                }

                event_count += events.size();

                nextToken = le_out.GetResult().GetNextToken();
            }

            else {
                std::cout << "Error looking up cloudtrail events"
                          << le_out.GetError().GetMessage()
                          << std::endl;
                break;
            }
        } while (!nextToken.empty() &&
                 (event_count < 100));    // Only show first 100 events.
    }

    Aws::ShutdownAPI(options);
    return 0;
}
