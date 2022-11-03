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
 * To run the example, refer to the instructions in the ReadMe.
 *
 */

#include "RDSDataHandler.h"
#include <aws/rds-data/RDSDataServiceClient.h>
#include <aws/rds-data/model/ExecuteStatementRequest.h>
#include <aws/rds/RDSClient.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/Document.h>
#include <array>

/**
 *  RDSDataHandler
 *
 *  Implementation of RDSDataReceiver which handles requests for data from a
 *  database table using Amazon RDS.
 *
 */

// snippet-start:[cpp.example_code.cross-service.serverless-aurora.RDSDataHandler]
namespace AwsDoc {
    namespace CrossService {
        /**
          *
          *  Constants for database column names.
          *
          */
        static const Aws::String ID_COLUMN("iditem");
        static const Aws::String NAME_COLUMN("username");
        static const Aws::String DESCRIPTION_COLUMN("description");
        static const Aws::String GUIDE_COLUMN("guide");
        static const Aws::String STATUS_COLUMN("status");
        static const Aws::String ARCHIVED_COLUMN("archived");

        //! Utility routine to get index of string in vector of strings.
        /*!
         \sa getIndexOf()
         \param vector: Vector of strings.
         \param string: Search string.
         \return size_t: Index of string or zero.
         */
        static size_t getIndexOf(std::vector<const Aws::String> vector,
                                 const Aws::String &string) {
            auto it = find(vector.begin(), vector.end(), string);
            size_t result = 0;
            if (it != vector.end()) {
                result = it - vector.begin();
            }
            else {
                std::cerr << "getIndexOf Error '" << string <<
                          "' not found." << std::endl;
            }
            return result;
        }
    }  // namespace CrossService
} // namespace AwsDoc

//! RDSDataHandler constructor.
/*!
 \sa RDSDataHandler::RDSDataHandler()
 \param database: Name of an Amazon RDS database.
 \param resourceArn: Amazon Resource Name (ARN) for an Amazon RDS.
 \param secretArn: Secret ARN for an Amazon RDS.
 \param tableName: Name of table to create in an Amazon RDS database.
 \param clientConfiguration: Aws client configuration.
 */
AwsDoc::CrossService::RDSDataHandler::RDSDataHandler(const Aws::String &database,
                                                     const Aws::String &resourceArn,
                                                     const Aws::String &secretArn,
                                                     const Aws::String &tableName,
                                                     const Aws::Client::ClientConfiguration &clientConfiguration)
        :
        mDatabase(database),
        mResourceArn(resourceArn),
        mSecretArn(secretArn),
        mTableName(tableName),
        mClientConfiguration(clientConfiguration) {
}

//! Routine executes a statement on an Amazon RDS database.
/*!
 \sa RDSDataHandler::executeStatement()
 \param sqlStatement: Sql statement as string.
 \param parameters: Vector of sql parameters.
 \return ExecuteStatementOutcome: Execute statement outcome.
 */
Aws::RDSDataService::Model::ExecuteStatementOutcome
AwsDoc::CrossService::RDSDataHandler::executeStatement(const Aws::String &sqlStatement,
                                                       std::vector<Aws::RDSDataService::Model::SqlParameter> parameters) {
    Aws::RDSDataService::RDSDataServiceClient client(mClientConfiguration);

    Aws::RDSDataService::Model::ExecuteStatementRequest request;
    request.SetDatabase(mDatabase);
    request.SetSecretArn(mSecretArn);
    request.SetResourceArn(mResourceArn);

    request.SetSql(sqlStatement);

    if (!parameters.empty()) {
        request.SetParameters(parameters);
    }

    return client.ExecuteStatement(request);
}

//! Routine which adds one work item.
/*!
 \sa RDSDataHandler::addWorkItem()
 \param workItem: Work item struct.
 \return bool: Successful completion.
 */
bool AwsDoc::CrossService::RDSDataHandler::addWorkItem(
        const AwsDoc::CrossService::WorkItem &workItem) {
    std::vector<const Aws::String> COLUMNS = {ID_COLUMN, NAME_COLUMN,
                                              DESCRIPTION_COLUMN,
                                              GUIDE_COLUMN, STATUS_COLUMN,
                                              ARCHIVED_COLUMN};

    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "INSERT INTO " << mTableName << " (";
    for (size_t i = 0; i < COLUMNS.size(); ++i) {
        sqlStream << COLUMNS[i];
        if (i < COLUMNS.size() - 1) {
            sqlStream << ", ";
        }
    }
    sqlStream << ") VALUES (";
    for (size_t i = 0; i < COLUMNS.size(); ++i) {
        sqlStream << ":" << COLUMNS[i];
        if (i < COLUMNS.size() - 1) {
            sqlStream << ", ";
        }
    }
    sqlStream << ")";

    // Create parameters vector and set it.
    std::string idItem = Aws::Utils::UUID::RandomUUID();

    // Add strings to parameters.
    std::vector<std::string> parameterNames =
            {ID_COLUMN, NAME_COLUMN, DESCRIPTION_COLUMN, GUIDE_COLUMN, STATUS_COLUMN};
    std::vector<std::string> parameterValues =
            {idItem, workItem.mName, workItem.mDescription, workItem.mGuide,
             workItem.mStatus};

    std::vector<Aws::RDSDataService::Model::SqlParameter> parameters;
    for (size_t i = 0; i < parameterNames.size(); ++i) {
        Aws::RDSDataService::Model::SqlParameter parameter;

        parameter.SetName(parameterNames[i]);

        Aws::RDSDataService::Model::Field field;
        field.SetStringValue(parameterValues[i]);
        parameter.SetValue(field);

        parameters.push_back(parameter);
    }

    // Add 'archived' boolean to parameters.
    Aws::RDSDataService::Model::SqlParameter parameter;

    parameter.SetName(ARCHIVED_COLUMN);

    Aws::RDSDataService::Model::Field field;
    field.SetLongValue(0);
    parameter.SetValue(field);

    parameters.push_back(parameter);

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str(), parameters);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully inserted '" << workItem.mName << "' into the table"
                  << std::endl;
    }
    else {
        std::cerr << "Error inserting '" << workItem.mName << "' into the table\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which retrieves a list of work items.
/*!
 \sa RDSDataHandler::getWorkItems()
 \param status: Filter for work item status.
 \param workItems: Vector of work items.
 \return bool: Successful completion.
 */
bool
AwsDoc::CrossService::RDSDataHandler::getWorkItems(WorkItemStatus status,
                                                   std::vector<WorkItem> &workItems) {
    std::vector<const Aws::String> COLUMNS = {ID_COLUMN, NAME_COLUMN,
                                              DESCRIPTION_COLUMN,
                                              GUIDE_COLUMN, STATUS_COLUMN,
                                              ARCHIVED_COLUMN};

    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "SELECT ";
    for (size_t i = 0; i < COLUMNS.size(); ++i) {
        sqlStream << COLUMNS[i];
        if (i < COLUMNS.size() - 1) {
            sqlStream << ", ";
        }
    }
    sqlStream << " FROM " << mTableName;

    if (status == WorkItemStatus::ARCHIVED) {
        sqlStream << " WHERE " << ARCHIVED_COLUMN << " = 1";
    }
    else if (status == WorkItemStatus::NOT_ARCHIVED) {
        sqlStream << " WHERE " << ARCHIVED_COLUMN << " = 0";
    }

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    if (outcome.IsSuccess()) {
        const std::vector<std::vector<Aws::RDSDataService::Model::Field>> &records =
                outcome.GetResult().GetRecords();

        std::cout << records.size() << " items retrieved with status ";
        switch (status) {
            case WorkItemStatus::ARCHIVED:
                std::cout << "archived.";
                break;
            case WorkItemStatus::NOT_ARCHIVED:
                std::cout << "not archived.";
                break;
            case WorkItemStatus::BOTH:
                std::cout << "archived and not archived.";
                break;
        }
        std::cout << std::endl;

        for (const std::vector<Aws::RDSDataService::Model::Field> &record: records) {
            WorkItem item;
            item.mID = record[getIndexOf(COLUMNS, ID_COLUMN)].GetStringValue();
            item.mName = record[getIndexOf(COLUMNS, NAME_COLUMN)].GetStringValue();
            item.mDescription = record[getIndexOf(COLUMNS,
                                                  DESCRIPTION_COLUMN)].GetStringValue();
            item.mGuide = record[getIndexOf(COLUMNS, GUIDE_COLUMN)].GetStringValue();
            item.mStatus = record[getIndexOf(COLUMNS, STATUS_COLUMN)].GetStringValue();
            item.mArchived =
                    record[getIndexOf(COLUMNS, ARCHIVED_COLUMN)].GetLongValue() > 0;
            workItems.push_back(item);
        }
    }
    else {
        std::cerr << "Error retrieving workItems.\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which updates a work item setting it as archived.
/*!
 \sa RDSDataHandler::setWorkItemToArchive()
 \param id: ID of work item.
 \return bool: Successful completion.
 */
bool AwsDoc::CrossService::RDSDataHandler::setWorkItemToArchive(const Aws::String &id) {
    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "UPDATE " << mTableName << " SET " << ARCHIVED_COLUMN << "=:"
              << ARCHIVED_COLUMN << " WHERE " << ID_COLUMN << "='" << id << "';";

    std::vector<Aws::RDSDataService::Model::SqlParameter> parameters;

    // Add 'archived' boolean to parameters.
    Aws::RDSDataService::Model::SqlParameter parameter;

    parameter.SetName(ARCHIVED_COLUMN);

    Aws::RDSDataService::Model::Field field;
    field.SetLongValue(1);
    parameter.SetValue(field);

    parameters.push_back(parameter);

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str(), parameters);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully updated work item with id '" << id
                  << "' to archived."
                  << std::endl;
    }
    else {
        std::cerr << "Error updating work item with id '" << id << "' to archived.\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which updates a work item's columns.
/*!
 \sa RDSDataHandler::updateWorkItem()
 \param workItem: Work item struct.
 \return bool: Successful completion.
 */
bool AwsDoc::CrossService::RDSDataHandler::updateWorkItem(
        const AwsDoc::CrossService::WorkItem &workItem) {
    std::vector<const Aws::String> COLUMNS = {NAME_COLUMN,
                                              DESCRIPTION_COLUMN,
                                              GUIDE_COLUMN, STATUS_COLUMN,
                                              ARCHIVED_COLUMN};

    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "UPDATE " << mTableName << " SET ";
    for (size_t i = 0; i < COLUMNS.size(); ++i) {
        sqlStream << COLUMNS[i] << "=:" << COLUMNS[i];
        if (i < COLUMNS.size() - 1) {
            sqlStream << ", ";
        }
    }
    sqlStream << " WHERE " << ID_COLUMN << "='" << workItem.mID << "';";

    // Add strings to parameters.
    std::vector<std::string> parameterNames =
            {NAME_COLUMN, DESCRIPTION_COLUMN, GUIDE_COLUMN, STATUS_COLUMN};
    std::vector<std::string> parameterValues =
            {workItem.mName, workItem.mDescription, workItem.mGuide,
             workItem.mStatus};

    std::vector<Aws::RDSDataService::Model::SqlParameter> parameters;
    for (size_t i = 0; i < parameterNames.size(); ++i) {
        Aws::RDSDataService::Model::SqlParameter parameter;

        parameter.SetName(parameterNames[i]);

        Aws::RDSDataService::Model::Field field;
        field.SetStringValue(parameterValues[i]);
        parameter.SetValue(field);

        parameters.push_back(parameter);
    }

    // Add 'archived' boolean to parameters.
    Aws::RDSDataService::Model::SqlParameter parameter;

    parameter.SetName(ARCHIVED_COLUMN);

    Aws::RDSDataService::Model::Field field;
    field.SetLongValue(workItem.mArchived ? 1 : 0);
    parameter.SetValue(field);

    parameters.push_back(parameter);

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str(), parameters);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully updated '" << workItem.mName << "' in the table"
                  << std::endl;
    }
    else {
        std::cerr << "Error updating '" << workItem.mName << "' in the table\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which retrieves one work item.
/*!
 \sa RDSDataHandler::getWorkItemWithId()
 \param id: ID of work item.
 \param workItem: Work item struct.
 \return bool: Successful completion.
 */
bool
AwsDoc::CrossService::RDSDataHandler::getWorkItemWithId(const Aws::String &id,
                                                        WorkItem &workItem) {
    std::vector<const Aws::String> COLUMNS = {ID_COLUMN, NAME_COLUMN,
                                              DESCRIPTION_COLUMN,
                                              GUIDE_COLUMN, STATUS_COLUMN,
                                              ARCHIVED_COLUMN};
    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "SELECT ";
    for (size_t i = 0; i < COLUMNS.size(); ++i) {
        sqlStream << COLUMNS[i];
        if (i < COLUMNS.size() - 1) {
            sqlStream << ", ";
        }
    }
    sqlStream << " FROM " << mTableName
              << " WHERE " << ID_COLUMN << " = '" << id << "'";

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    if (outcome.IsSuccess()) {
        const std::vector<std::vector<Aws::RDSDataService::Model::Field>> &records =
                outcome.GetResult().GetRecords();

        std::cout << records.size() << " items retrieved." << std::endl;
        if (records.size() > 0) {
            const std::vector<Aws::RDSDataService::Model::Field> &record = records[0];
            workItem.mID = record[getIndexOf(COLUMNS, ID_COLUMN)].GetStringValue();
            workItem.mName = record[getIndexOf(COLUMNS, NAME_COLUMN)].GetStringValue();
            workItem.mDescription = record[getIndexOf(COLUMNS,
                                                      DESCRIPTION_COLUMN)].GetStringValue();
            workItem.mGuide = record[getIndexOf(COLUMNS,
                                                GUIDE_COLUMN)].GetStringValue();
            workItem.mStatus = record[getIndexOf(COLUMNS,
                                                 STATUS_COLUMN)].GetStringValue();
            workItem.mArchived =
                    record[getIndexOf(COLUMNS, ARCHIVED_COLUMN)].GetLongValue() > 0;
        }
        else {
            std::cerr << "Error no items retrieved for iD " << id << std::endl;
        }
    }
    else {
        std::cerr << "Error retrieving workItems.\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();

}

//! Routine which creates an Amazon RDS database table if the table does not
//! already exist.
/*!
 \sa RDSDataHandler::initializeTable()
 \param recreateTable: If true, always create blank table.
 \return bool: Successful completion.
 */
bool AwsDoc::CrossService::RDSDataHandler::initializeTable(bool recreateTable) {
    bool result = true;

    if (!tableExists(mTableName)) {
        createTable(mTableName);
    }
    else if (recreateTable) {
        deleteTable(mTableName);
        createTable(mTableName);
    }

    return result;
}

//! Routine which queries if an Amazon RDS database table exists.
/*!
 \sa RDSDataHandler::tableExists()
 \param tableName: Table name.
 \return bool: True if table exists.
 */
bool AwsDoc::CrossService::RDSDataHandler::tableExists(const Aws::String &tableName) {
    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "SHOW TABLES IN " << mDatabase;

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    bool result = false;
    if (outcome.IsSuccess()) {
        const std::vector<std::vector<Aws::RDSDataService::Model::Field>> &records =
                outcome.GetResult().GetRecords();

        for (const std::vector<Aws::RDSDataService::Model::Field> &record: records) {
            if (!record.empty()) {
                if (record[0].GetStringValue() == mTableName) {
                    result = true;
                    break;
                }
            }
        }
    }
    else {
        std::cerr << "Error for query '" << sqlStream.str() << "'\n"
                  << outcome.GetError().GetMessage() << std::endl;
    }

    std::cout << "Table '" << tableName << (result ? "' exists." : "' doesn't exist.")
              << std::endl;

    return result;
}

//! Routine which creates a table in an Amazon RDS database.
/*!
 \sa RDSDataHandler::createTable()
 \param tableName: Table name.
 \return bool: Successful completion.
 */
bool AwsDoc::CrossService::RDSDataHandler::createTable(const Aws::String &tableName) {
    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "CREATE TABLE " << tableName << " ("
              << ID_COLUMN << " VARCHAR(45), "
              << NAME_COLUMN << " VARCHAR(45), "
              << DESCRIPTION_COLUMN << " VARCHAR(400), "
              << GUIDE_COLUMN << " VARCHAR(45), "
              << STATUS_COLUMN << " VARCHAR(400), "
              << ARCHIVED_COLUMN << "  TINYINT(4));";

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    if (outcome.IsSuccess()) {
        std::cout << "Successfully created table '" << tableName << "'." << std::endl;
    }
    else {
        std::cerr << "Error creating table '" << tableName << "'.\n"
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which deletes a table in an Amazon RDS database.
/*!
 \sa RDSDataHandler::deleteTable()
 \param tableName: Table name.
 \return bool: Successful completion.
 */
bool AwsDoc::CrossService::RDSDataHandler::deleteTable(const Aws::String &tableName) {
    // Create a sql statement.
    std::stringstream sqlStream;
    sqlStream << "DROP TABLE " << tableName;

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    if (outcome.IsSuccess()) {
        std::cout << "Successfully deleted table '" << tableName << "'." << std::endl;
    }
    else {
        std::cerr << "Error deleting table '" << tableName << "'.\n"
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.cross-service.serverless-aurora.RDSDataHandler.constants]


