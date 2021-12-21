//snippet-sourcedescription:[update_item.cpp demonstrates how to update an item in an Amazon DynamoDB table.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/30/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[dynamodb.cpp.update_item.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/UpdateItemRequest.h>
#include <aws/dynamodb/model/UpdateItemResult.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.update_item.inc]


/*
   Update a DynamoDB table item
 
   Accepts the table name, the key value to update, the attribute name
   to update, and the new attribute value. If an attribute of the specified
   name does not exist, it is added to the key value.
 
  The specified table must have a key called "id".
 
  The example code only sets/updates an attribute value. It processes
  the attribute value as a string, even if the value could be interpreted
  as a number. Also, the example code does not remove an existing attribute
  from the key value. 
  
   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
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
        const Aws::String tableName = (argv[1]);
        const Aws::String keyValue =  (argv[2]);
        const Aws::String attributeNameAndValue = (argv[3]);

        // snippet-start:[dynamodb.cpp.update_item.code]
        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        // *** Define UpdateItem request arguments
        // Define TableName argument.
        Aws::DynamoDB::Model::UpdateItemRequest request;
        request.SetTableName(tableName);

        // Define KeyName argument.
        Aws::DynamoDB::Model::AttributeValue attribValue;
        attribValue.SetS(keyValue);
        request.AddKey("id", attribValue);

        // Construct the SET update expression argument.
        Aws::String update_expression("SET #a = :valueA");
        request.SetUpdateExpression(update_expression);

        // Parse the attribute name and value. Syntax: "name=value".
        auto parsed = Aws::Utils::StringUtils::Split(attributeNameAndValue, '=');
        
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

        // Construct attribute value argument.
        Aws::DynamoDB::Model::AttributeValue attributeUpdatedValue;
        attributeUpdatedValue.SetS(parsed[1]);
        Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> expressionAttributeValues;
        expressionAttributeValues[":valueA"] = attributeUpdatedValue;
        request.SetExpressionAttributeValues(expressionAttributeValues);

        // Update the item.
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
