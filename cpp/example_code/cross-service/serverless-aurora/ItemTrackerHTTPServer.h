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
            ARCHIVED,
            NOT_ARCHIVED,
            BOTH
        };
        struct WorkItem
        {
            Aws::String mID;
            Aws::String mName;
            Aws::String mDate;
            Aws::String mGuide;
            Aws::String mDescription;
            Aws::String mStatus;
            bool mArchived;

        };
        class RDSDataReceiver {
        public:
            virtual bool addWorkItem(const WorkItem& workItem) = 0;

            virtual std::vector<WorkItem> getWorkItems(WorkItemStatus status) = 0;

            virtual WorkItem getWorkItemWithId(const Aws::String& id) = 0;
        };
        class SESEmailReceiver {
        public:
            virtual bool sendEmail(const Aws::String emailAddress) = 0;
        };

        class ItemTrackerHTTPServer {
        public:
            explicit ItemTrackerHTTPServer(RDSDataReceiver& rdsDataReceiver);

            std::string getWorkItemJSON(WorkItemStatus status);

            std::string getWorkItemWithIdJson(const Aws::String& id);

            void addWorkItem(const std::string& workItemJson);

            void sendEmail(const std::string& emailJson);

            void run(int argc, char** argv);
        private:

            static WorkItem jsonToWorkItem(const std::string& jsonString);

            RDSDataReceiver& mRdsDataReceiver;
            std::mutex mHTTPMutex;  // HTTP is received asynchronously.
                                    // The lock ensures the get items following
                                    // a post item contains the posted record.
        };
    }  // namespace CrossService
} // namespace AwsDoc


#endif //SERVERLESSAURORA_ITEMTRACKERSERVER_H
