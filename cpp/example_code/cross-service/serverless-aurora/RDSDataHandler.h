/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 *  RDSDataHandler.h/.cpp
 *
 *  The code in these 2 file implements the creation, updating and querying of a table in
 *  an Amazon Relational Database Service (Amazon RDS).
 *
 *  To run the example, refer to the instructions in the ReadMe.
 */

#pragma once
#ifndef EXAMPLECODE_RDSDATAHANDLER_H
#define EXAMPLECODE_RDSDATAHANDLER_H

#include "ItemTrackerHTTPHandler.h"
#include <aws/core/client/ClientConfiguration.h>
#include <aws/rds-data/RDSDataServiceClient.h>
#include <aws/rds-data/model/SqlParameter.h>

namespace AwsDoc {
    namespace CrossService {
        /**
         *  RDSDataHandler
         *
         *  Implementation of RDSDataReceiver which handles requests for data from a
         *  database table using Amazon RDS.
         *
         */
        class RDSDataHandler : public AwsDoc::CrossService::RDSDataReceiver {
        public:

            //! RDSDataHandler constructor.
            /*!
             \sa RDSDataHandler::RDSDataHandler()
             \param database: Name of an Amazon RDS database.
             \param resourceArn: Amazon Resource Name (ARN) for an Amazon RDS.
             \param secretArn: Secret ARN for an Amazon RDS.
             \param tableName: Name of table to create in an Amazon RDS database.
             \param clientConfiguration: Aws client configuration.
             */
            RDSDataHandler(const Aws::String &database,
                           const Aws::String &resourceArn,
                           const Aws::String &secretArn,
                           const Aws::String &tableName,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

            //! Routine which creates if table does not exist.
            /*!
             \sa RDSDataHandler::initializeTable()
             \param recreateTable: If true, always create blank table.
             \return bool: Successful completion.
             */
            bool initializeTable(bool recreateTable);

            //! Routine which updates a work item setting it as archived.
            /*!
             \sa RDSDataHandler::setWorkItemToArchive()
             \param id: ID of work item.
             \return bool: Successful completion.
             */
            bool setWorkItemToArchive(const Aws::String &id) override;

            //! Routine which updates a work item's columns.
            /*!
             \sa RDSDataHandler::updateWorkItem()
             \param workItem: Work item struct.
             \return bool: Successful completion.
             */
            bool updateWorkItem(const WorkItem &workItem) override;

            //! Routine which retrieves one work item.
            /*!
             \sa RDSDataHandler::getWorkItemWithId()
             \param id: ID of work item.
             \param workItem: Work item struct.
             \return bool: Successful completion.
             */
            bool getWorkItemWithId(const Aws::String &id, WorkItem &workItem) override;

            //! Routine which adds one work item.
            /*!
             \sa RDSDataHandler::addWorkItem()
             \param workItem: Work item struct.
             \return bool: Successful completion.
             */
            virtual bool addWorkItem(const WorkItem &workItem) override;

            //! Routine which retrieves a list of work items.
            /*!
             \sa RDSDataHandler::getWorkItems()
             \param status: Filter for work item status.
             \param workItems: Vector of work items.
             \return bool: Successful completion.
             */
            virtual bool getWorkItems(WorkItemStatus status,
                                      std::vector<WorkItem> &workItems) override;

        private:

            bool tableExists(const Aws::String &tableName);

            bool createTable(const Aws::String &tableName);

            bool deleteTable(const Aws::String &tableName);

            Aws::RDSDataService::Model::ExecuteStatementOutcome executeStatement(
                    const Aws::String &sqlStatement,
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
