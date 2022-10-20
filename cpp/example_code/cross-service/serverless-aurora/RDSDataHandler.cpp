/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "RDSDataHandler.h"
#include <aws/rds-data/RDSDataServiceClient.h>
#include <aws/rds-data/model/ExecuteStatementRequest.h>
#include <aws/rds/RDSClient.h>

#include <aws/core/utils/Document.h>
#include <array>

AwsDoc::CrossService::RDSDataHandler::RDSDataHandler(const Aws::String &database,
                                                     const Aws::String &resourceArn,
                                                     const Aws::String &secretArn,
                                                     const Aws::String& tableName,
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
    std::cout << "addWorkItem " <<
              " item.mName " << workItem.mName <<
              " workItem.mGuide " << workItem.mGuide <<
              " workItem.mDescription " << workItem.mDescription <<
              " workItem.mStatus " << workItem.mStatus << std::endl;

    Aws::RDSDataService::RDSDataServiceClient client(mClientConfiguration);

    Aws::RDSDataService::Model::ExecuteStatementRequest request;
    request.SetDatabase(mDatabase);
    request.SetSecretArn(mSecretArn);
    request.SetResourceArn(mResourceArn);

    // Create SQL string and set it.
    std::stringstream  sqlStream;
    sqlStream << "INSERT INTO " << mTableName << " (username, description, guide, status) "
              << " VALUES (:username, :description, :guide, :status)";

    request.SetSql(sqlStream.str());

    // Create parameters vector and set it.
    std::array<std::string, 4> parameterNames = {"username", "description",
                                       "guide", "status"};
    std::array<std::string, 4> parameterValues = {workItem.mName, workItem.mDescription,
                                         workItem.mGuide, workItem.mStatus};

    std::vector<Aws::RDSDataService::Model::SqlParameter> parameters;
    for (size_t i = 0; i < parameterNames.size(); ++i)
    {
        Aws::RDSDataService::Model::SqlParameter parameter;
        parameter.SetName(parameterNames[i]);
        Aws::RDSDataService::Model::Field field;
        field.SetStringValue(parameterValues[i]);
        parameter.SetValue(field);

        parameters.push_back(parameter);
    }
    request.SetParameters(parameters);

     Aws::RDSDataService::Model::ExecuteStatementOutcome outcome =
            client.ExecuteStatement(request);

    if (outcome.IsSuccess())
    {
        std::cout << "Successfully inserted '" << workItem.mName << "' into the table"
        << std::endl;
    }
    else{
        std::cerr << "Error inserting '" << workItem.mName << "' into the table\n"
        << "Error: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

std::vector<AwsDoc::CrossService::WorkItem>
AwsDoc::CrossService::RDSDataHandler::getWorkItems(
        AwsDoc::CrossService::WorkItemStatus status) {
    std::vector<WorkItem> result;
    for (int i = 0; i < 3; ++i) {
        WorkItem item;
        item.mID = std::to_string(i);
        item.mName = std::to_string(i) + " name";
        item.mGuide = std::to_string(i) + " guide";
        item.mDescription = std::to_string(i) + " description";
        item.mStatus = (status == AwsDoc::CrossService::WorkItemStatus::ACTIVE) ?
                       "active" : "archived";
        item.mDate = "2022-10-17T07:20:45Z";
        result.push_back(item);
    }
    return result;
}

