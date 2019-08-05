 
//snippet-sourcedescription:[update_item.cpp demonstrates how to update an item in an Amazon DynamoDB table.]
//snippet-service:[dynamodb]
//snippet-keyword:[Amazon DynamoDB]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05-24-2019]
//snippet-sourceauthor:[AWS]


/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
//snippet-start:[dynamodb.cpp.update_item.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/UpdateItemRequest.h>
#include <aws/dynamodb/model/UpdateItemResult.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.update_item.inc]


/**
 * Update a DynamoDB table item
 *
 * Accepts the table name, the key value to update, the attribute name
 * to update, and the new attribute value. If an attribute of the specified
 * name does not exist, it is added to the key value.
 *
 * The specified table must have a key called "Name".
 * 
 * The example code only sets/updates an attribute value. It processes
 * the attribute value as a string, even if the value could be interpreted 
 * as a number. Also, the example code does not remove an existing attribute
 * from the key value. Adding support for number values or removal of an
 * attribute would require simple modifications that should be self-evident.
 */
int main(int argc, char** argv)
{
    const std::string USAGE = "\n"
        "Usage:\n"
        "    update_item <tableName> <keyValue> <attribute=value> ..\n\n"
        "Where:\n"
        "    tableName       - name of the table to put the item in\n"
        "    keyValue        - the key value to update\n"
        "    attribute=value - attribute=updated value\n\n"
        "Examples:\n"
        "    update_item SiteColors text default=000000\n"
        "    update_item SiteColors background code=d3d3d3\n\n";

    if (argc < 4)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String tableName(argv[1]);
        const Aws::String keyValue(argv[2]);
        const Aws::String attributeNameAndValue(argv[3]);

        // snippet-start:[dynamodb.cpp.update_item.code]
        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        // *** Define UpdateItem request arguments
        // Define TableName argument
        Aws::DynamoDB::Model::UpdateItemRequest request;
        request.SetTableName(tableName);

        // Define KeyName argument
        Aws::DynamoDB::Model::AttributeValue attribValue;
        attribValue.SetS(keyValue);
        request.AddKey("Name", attribValue);

        // Construct the SET update expression argument
        Aws::String update_expression("SET #a = :valueA");
        request.SetUpdateExpression(update_expression);

        // Parse the attribute name and value. Syntax: "name=value"
        auto parsed = Aws::Utils::StringUtils::Split(attributeNameAndValue, '=');
        // parsed[0] == attribute name, parsed[1] == attribute value
        if (parsed.size() != 2)
        {
            std::cout << "Invalid argument syntax: " << attributeNameAndValue << USAGE;
            return 1;
        }

        // Construct attribute name argument
        // Note: Setting the ExpressionAttributeNames argument is required only
        // when the name is a reserved word, such as "default". Otherwise, the 
        // name can be included in the update_expression, as in 
        // "SET MyAttributeName = :valueA"
        Aws::Map<Aws::String, Aws::String> expressionAttributeNames;
        expressionAttributeNames["#a"] = parsed[0];
        request.SetExpressionAttributeNames(expressionAttributeNames);

        // Construct attribute value argument
        Aws::DynamoDB::Model::AttributeValue attributeUpdatedValue;
        attributeUpdatedValue.SetS(parsed[1]);
        Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> expressionAttributeValues;
        expressionAttributeValues[":valueA"] = attributeUpdatedValue;
        request.SetExpressionAttributeValues(expressionAttributeValues);

        // Update the item
        const Aws::DynamoDB::Model::UpdateItemOutcome& result = dynamoClient.UpdateItem(request);
        if (!result.IsSuccess())
        {
            std::cout << result.GetError().GetMessage() << std::endl;
            return 1;
        }
        std::cout << "Item was updated" << std::endl;
        // snippet-end:[dynamodb.cpp.update_item.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}