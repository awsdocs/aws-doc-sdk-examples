/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "RDSDataHandler.h"
#include <aws/rds-data/RDSDataServiceClient.h>
#include <aws/rds-data/model/ExecuteStatementRequest.h>
#include <aws/rds/RDSClient.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/Document.h>
#include <array>

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


bool AwsDoc::CrossService::RDSDataHandler::addWorkItem(
        const AwsDoc::CrossService::WorkItem &workItem) {
    // Create SQL statement.
    std::stringstream sqlStream;
    sqlStream << "INSERT INTO " << mTableName <<
              " (iditem, username, description, guide, status, archived) "
              << " VALUES " <<
              "(:iditem, :username, :description, :guide, :status, :archived)";

    // Create parameters vector and set it.
    std::string idItem = Aws::Utils::UUID::RandomUUID();

    // Add strings to parameters.
    std::vector<std::string> parameterNames =
            {"iditem", "username", "description", "guide", "status"};
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

    parameter.SetName("archived");

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

std::vector<AwsDoc::CrossService::WorkItem>
AwsDoc::CrossService::RDSDataHandler::getWorkItems(WorkItemStatus status) {
    std::vector<WorkItem> result;

    std::stringstream sqlStream;
    sqlStream << "SELECT iditem, username, description, "
              << "guide, status, archived FROM " << mTableName;

    if (status == WorkItemStatus::ARCHIVED)
    {
        sqlStream << " WHERE archived = 1";
    }
    else  if (status == WorkItemStatus::NOT_ARCHIVED)
    {
        sqlStream << " WHERE archived = 0";
    }

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    if (outcome.IsSuccess()) {
        const std::vector<std::vector<Aws::RDSDataService::Model::Field>> &records =
                outcome.GetResult().GetRecords();

        std::cout << records.size() << " items retrieved." << std::endl;
        for (const std::vector<Aws::RDSDataService::Model::Field> &record: records) {
            WorkItem item;
            item.mID = record[0].GetStringValue();
            item.mID = item.mID.substr(0, std::min(4,
                                                   (int) item.mID.length())); // TODO(Steve) remove substring
            item.mName = record[1].GetStringValue();
            item.mDescription = record[2].GetStringValue();
            item.mGuide = record[3].GetStringValue();
            item.mStatus = record[4].GetStringValue();
            item.mArchived = record[5].GetBooleanValue();
            result.push_back(item);
        }
    }
    else {
        std::cerr << "Error retrieving workItems.\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return result;
}

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

bool AwsDoc::CrossService::RDSDataHandler::tableExists(const Aws::String &tableName) {
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

    std::cout << "Table '" << tableName <<  (result ? "' exists." : "' doesn't exist.") <<
    std::endl;

    return result;
}

bool AwsDoc::CrossService::RDSDataHandler::createTable(const Aws::String &tableName) {
    std::stringstream sqlStream;
    sqlStream << "CREATE TABLE " << tableName << " (iditem VARCHAR(45), "
              << "description VARCHAR(400), guide VARCHAR(45), status VARCHAR(400), "
              << "username VARCHAR(45), archived TINYINT(4));";

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

bool AwsDoc::CrossService::RDSDataHandler::initializeTable(bool recreateTable) {
    bool result = true;

    if (!tableExists(mTableName)) {
        createTable(mTableName);
    }
    else if (recreateTable)
    {
        deleteTable(mTableName);
        createTable(mTableName);
    }

    return result;
}

bool AwsDoc::CrossService::RDSDataHandler::deleteTable(const Aws::String &tableName) {
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

AwsDoc::CrossService::WorkItem
AwsDoc::CrossService::RDSDataHandler::getWorkItemWithId(const Aws::String &id) {
    WorkItem result;

    std::stringstream sqlStream;
    sqlStream << "SELECT iditem, username, description, "
              << "guide, status, archived FROM " << mTableName
              << " WHERE iditem = '" << id << "'";

    Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            executeStatement(sqlStream.str());

    if (outcome.IsSuccess()) {
        const std::vector<std::vector<Aws::RDSDataService::Model::Field>> &records =
                outcome.GetResult().GetRecords();

        std::cout << records.size() << " items retrieved." << std::endl;
        if (records.size() > 0)
        {
            const std::vector<Aws::RDSDataService::Model::Field> &record = records[0];
            result.mID = record[0].GetStringValue();
            result.mID = result.mID.substr(0, std::min(4,
                                                   (int) result.mID.length())); // TODO(Steve) remove substring
            result.mName = record[1].GetStringValue();
            result.mDescription = record[2].GetStringValue();
            result.mGuide = record[3].GetStringValue();
            result.mStatus = record[4].GetStringValue();
            result.mArchived = record[5].GetBooleanValue();
        }
        else{
            std::cerr << "Error no items retrieved for iD " << id << std::endl;
        }
    }
    else {
        std::cerr << "Error retrieving workItems.\n"
                  << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return result;

}


