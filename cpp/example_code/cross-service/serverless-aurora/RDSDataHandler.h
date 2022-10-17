/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef EXAMPLECODE_RDSDATAHANDLER_H
#define EXAMPLECODE_RDSDATAHANDLER_H

#include "ItemTrackerHTTPServer.h"

namespace AwsDoc {
    namespace CrossService {
        class RDSDataHandler : public AwsDoc::CrossService::RDSDataReceiver {
            virtual bool setWorkItem(const WorkItem &workItem) override;

            virtual std::vector<WorkItem> getWorkItems(WorkItemStatus status) override;
        };
    }  // namespace CrossService
} // namespace AwsDoc


#endif //EXAMPLECODE_RDSDATAHANDLER_H
