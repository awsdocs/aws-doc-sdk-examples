/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/core/Aws.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/BatchWriteItemRequest.h>
#include <iostream>
#include <fstream>
#include "dynamodb_samples.h"

namespace AwsDoc {
    namespace DynamoDB {

        static bool addWriteRequests(const Aws::String& tableName,
                                     const Aws::Utils::Array<Aws::Utils::Json::JsonView>& requestsJson,
                                     Aws::Vector<Aws::DynamoDB::Model::WriteRequest>& writeRequests);

         static bool getAttributeObjectsMap(const Aws::Utils::Json::JsonView &jsonView,
                                            Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &attributes);
    } //  namespace DynamoDB
} // namespace AwsDoc

//! Batch write items from a JSON file.
/*!
  \sa batchWriteItem()
  \param jsonFilePath: JSON file path.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */

/*
 * This routine takes as input a JSON file in a format similar to the sample data
 * that can be downloaded from the following URL.
 * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SampleData.html.
 *
 * The JSON input is in the format used in the BatchWriteItem api request. The JSON strings are
 * converted to AttributeValue objects which will then output JSON strings for the api request.
 * The JSON output is essentially the same as the JSON input.
 *
 * This is perhaps an artificial example, but it may be helpful in demonstrating the APIs.
 */

bool AwsDoc::DynamoDB::batchWriteItem(const Aws::String &jsonFilePath,
                    const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::ifstream fileStream(jsonFilePath);

    if (!fileStream) {
        std::cerr << "Error: could not open file '" << jsonFilePath << "'." << std::endl;
    }

    std::stringstream stringStream;
    stringStream << fileStream.rdbuf();
    Aws::Utils::Json::JsonValue jsonValue(stringStream);

    Aws::DynamoDB::Model::BatchWriteItemRequest batchWriteItemRequest;
    Aws::Map<Aws::String, Aws::Utils::Json::JsonView> level1Map = jsonValue.View().GetAllObjects();
    for (const auto &level1Entry: level1Map) {
        const Aws::Utils::Json::JsonView &entriesView = level1Entry.second;
        const Aws::String& tableName = level1Entry.first;
        // The JSON entries at this level are:
        //  key - table name
        //  value - list of request objects
        if (!entriesView.IsListType()) {
            std::cerr << "Error: JSON file entry '"
                      << tableName << "' is not a list." << std::endl;
            continue;
        }

        Aws::Utils::Array<Aws::Utils::Json::JsonView> entries = entriesView.AsArray();

        Aws::Vector<Aws::DynamoDB::Model::WriteRequest> writeRequests;
        if (AwsDoc::DynamoDB::addWriteRequests(tableName, entries,
                                               writeRequests))
        {
            batchWriteItemRequest.AddRequestItems(tableName, writeRequests);
        }
    }

    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::BatchWriteItemOutcome outcome = dynamoClient.BatchWriteItem(batchWriteItemRequest);

    if (outcome.IsSuccess()) {
        std::cout << "DynamoDB::BatchWriteItem was successful." << std::endl;
    }
    else {
        std::cerr << "Error with DynamoDB::BatchWriteItem. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return true;
}


bool AwsDoc::DynamoDB::addWriteRequests(const Aws::String& tableName,
                                        const Aws::Utils::Array<Aws::Utils::Json::JsonView>& requestsJson,
                                        Aws::Vector<Aws::DynamoDB::Model::WriteRequest>& writeRequests)
{
    for (size_t i = 0; i < requestsJson.GetLength(); ++i)
    {
        const Aws::Utils::Json::JsonView& requestsEntry = requestsJson[i];
        if (!requestsEntry.IsObject())
        {
            std::cerr << "Error: incorrect requestsEntry type " << requestsEntry.WriteReadable() << std::endl;
            return false;
        }

        Aws::Map<Aws::String, Aws::Utils::Json::JsonView> requestsMap = requestsEntry.GetAllObjects();

        for (const auto& request : requestsMap)
        {
            const Aws::String& requestType = request.first;
            const Aws::Utils::Json::JsonView& requestJsonView = request.second;

            if (requestType == "PutRequest")
            {
                if (!requestJsonView.ValueExists("Item"))
                {
                    std::cerr << "Error: item key missing for requests " << requestJsonView.WriteReadable() << std::endl;
                    return false;
                }
                Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> attributes;
                if (!getAttributeObjectsMap(requestJsonView.GetObject("Item"),
                                            attributes))
                {
                    std::cerr << "Error getting attributes " << requestJsonView.WriteReadable() << std::endl;
                    return false;
                }

                Aws::DynamoDB::Model::PutRequest putRequest;
                putRequest.SetItem(attributes);
                writeRequests.push_back(Aws::DynamoDB::Model::WriteRequest().WithPutRequest(putRequest));
            }
            else
            {
                std::cerr << "Error: unimplemented request type '" << requestType << "'." << std::endl;
            }
        }
    }

    return true;
}

bool AwsDoc::DynamoDB::getAttributeObjectsMap(const Aws::Utils::Json::JsonView &jsonView,
                                              Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &attributes)
{
    Aws::Map<Aws::String, Aws::Utils::Json::JsonView>  objectsMap = jsonView.GetAllObjects();
    for (const auto& entry : objectsMap)
    {
        const Aws::String& attributeKey = entry.first;
        const Aws::Utils::Json::JsonView& attributeJsonView = entry.second;

        if (!attributeJsonView.IsObject())
        {
            std::cerr << "Error: attribute not an object " << attributeJsonView.WriteReadable() << std::endl;
            return false;
        }

        attributes.emplace(attributeKey, Aws::DynamoDB::Model::AttributeValue(attributeJsonView));
    }

    return true;
}



#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 2) {
        std::cout << R"(
Usage:
    run_query_items <table_name> <partition_key> <partition_value> [projection_expression]
Where:
    table_name - the table to get an item from.
    partition_key  - Partition Key attribute of the table.
    partition_value  - Partition Key value to query.)";
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String jsonFilePath = (argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::batchWriteItem(jsonFilePath, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif //  TESTING_BUILD