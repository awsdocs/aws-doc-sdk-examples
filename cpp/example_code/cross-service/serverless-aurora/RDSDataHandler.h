/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef EXAMPLECODE_RDSDATAHANDLER_H
#define EXAMPLECODE_RDSDATAHANDLER_H

#include "ItemTrackerHTTPServer.h"
#include <aws/core/client/ClientConfiguration.h>
#include <aws/rds-data/RDSDataServiceClient.h>
#include <aws/rds-data/model/SqlParameter.h>

namespace AwsDoc {
    namespace CrossService {
        class RDSDataHandler : public AwsDoc::CrossService::RDSDataReceiver {
        public:
            RDSDataHandler(const Aws::String& database,
                           const Aws::String& resourceArn,
                           const Aws::String& secretArn,
                           const Aws::String& tableName,
                           const Aws::Client::ClientConfiguration& clientConfiguration);

            bool initializeTable(bool recreateTable);

            WorkItem getWorkItemWithId(const Aws::String &id) override;

            virtual bool addWorkItem(const WorkItem &workItem) override;

            virtual std::vector<WorkItem> getWorkItems(WorkItemStatus status) override;

        private:

            bool tableExists(const Aws::String &tableName);

            bool createTable(const Aws::String& tableName);

            bool deleteTable(const Aws::String& tableName);

            Aws::RDSDataService::Model::ExecuteStatementOutcome executeStatement(
                    const Aws::String& sqlStatement,
                    std::vector<Aws::RDSDataService::Model::SqlParameter> parameters =
                    std::vector<Aws::RDSDataService::Model::SqlParameter>());

            Aws::String mDatabase;
            Aws::String mResourceArn;
            Aws::String mSecretArn;
            Aws::String mTableName;
            Aws::Client::ClientConfiguration mClientConfiguration;

        };
    }  // namespace CrossService
} // namespace AwsDoc


#endif //EXAMPLECODE_RDSDATAHANDLER_H
