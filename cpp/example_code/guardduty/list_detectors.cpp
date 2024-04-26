// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/guardduty/GuardDutyClient.h>
#include <aws/guardduty/model/ListDetectorsRequest.h>
#include <aws/guardduty/model/ListDetectorsResult.h>
#include <iostream>

/*
 * Lists GuardDuty detectors in the current AWS region.
 */

int main(int argc, char **argv) {
    if (argc != 1) {
        std::cout << "Usage: list_detectors" << std::endl;

        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::GuardDuty::GuardDutyClient gd;

        Aws::GuardDuty::Model::ListDetectorsRequest ld_req;

        Aws::String next_token; // Used for pagination.
        Aws::Vector<Aws::String> all_detector_ids;

        do {
            if (!next_token.empty()) {
                ld_req.SetNextToken(next_token);
            }
            auto ld_out = gd.ListDetectors(ld_req);

            if (ld_out.IsSuccess()) {
                const auto &detector_ids = ld_out.GetResult().GetDetectorIds();
                all_detector_ids.insert(all_detector_ids.end(), detector_ids.cbegin(),
                                        detector_ids.cend());

                next_token = ld_out.GetResult().GetNextToken();
            }
            else {
                std::cout << "Error listing the detectors "
                          << ld_out.GetError().GetMessage()
                          << std::endl;
                break;
            }

        } while (!next_token.empty());

        std::cout << all_detector_ids.size() << " detector(s) listed." << std::endl;
        for (auto detector_id: all_detector_ids) {
            std::cout << "  " << detector_id << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}

