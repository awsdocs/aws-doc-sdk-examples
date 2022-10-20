/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef EXAMPLECODE_RDSDATAHANDLER_H
#define EXAMPLECODE_RDSDATAHANDLER_H

#include "ItemTrackerHTTPServer.h"
#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace CrossService {
        class RDSDataHandler : public AwsDoc::CrossService::RDSDataReceiver {
        public:
            RDSDataHandler(const Aws::String& database,
                           const Aws::String& resourceArn,
                           const Aws::String& secretArn,
                           const Aws::String& tableName,
                           const Aws::Client::ClientConfiguration& clientConfiguration);

            virtual bool addWorkItem(const WorkItem &workItem) override;

            virtual std::vector<WorkItem> getWorkItems(WorkItemStatus status) override;

        private:

            Aws::String mDatabase;
            Aws::String mResourceArn;
            Aws::String mSecretArn;
            Aws::String mTableName;
            Aws::Client::ClientConfiguration mClientConfiguration;

        };
    }  // namespace CrossService
} // namespace AwsDoc


#endif //EXAMPLECODE_RDSDATAHANDLER_H
