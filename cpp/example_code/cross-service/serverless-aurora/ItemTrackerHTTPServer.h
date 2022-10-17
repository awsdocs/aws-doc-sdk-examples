/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SERVERLESSAURORA_ITEMTRACKERSERVER_H
#define SERVERLESSAURORA_ITEMTRACKERSERVER_H
#include <aws/core/Aws.h>

namespace AwsDoc {
    namespace CrossService {
        enum WorkItemStatus {
            ACTIVE,
            ARCHIVED
        };
        struct WorkItem
        {
            Aws::String mID;
            Aws::String mName;
            Aws::String mDate;
            Aws::String mGuide;
            Aws::String mDescription;
            Aws::String mStatus;

        };
        class RDSDataReceiver {
        public:
            virtual bool setWorkItem(const WorkItem& workItem) = 0;

            virtual std::vector<WorkItem> getWorkItems(WorkItemStatus status) = 0;
        };
        class ItemTrackerHTTPServer {
        public:
            explicit ItemTrackerHTTPServer(RDSDataReceiver& rdsDataReceiver);

            std::string getWorkItemJSON(WorkItemStatus status);

            void addWorkItem(const std::string& workItemJson);

            void run(int argc, char** argv);
        private:

            static WorkItem jsonToWorkItem(const std::string& jsonString);

            RDSDataReceiver& mRdsDataReceiver;

        };
    }  // namespace CrossService
} // namespace AwsDoc


#endif //SERVERLESSAURORA_ITEMTRACKERSERVER_H
