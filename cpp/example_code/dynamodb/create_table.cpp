/*
Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/KeySchemaElement.h>
#include <aws/dynamodb/model/ProvisionedThroughput.h>
#include <aws/dynamodb/model/ScalarAttributeType.h>
#include <iostream>


/**
* Deletes a cloud watch alarm, based on command line input
*/
int main(int argc, char** argv)
{
	const Aws::String USAGE = "\n" \
		"Usage:\n"
		"    create_table <table> <optional:region>\n\n"
		"Where:\n"
		"    table - the table to create\n"
		"    region- optional region\n\n"
		"Example:\n"
		"    create_table HelloTable us-west-2\n";

	if (argc < 2) {
		std::cout << USAGE;
		return 1;
	}

	const Aws::String table(argv[1]);
	const Aws::String region(argc > 2 ? argv[2] : "");
	Aws::SDKOptions options;

	Aws::InitAPI(options);
	{
		Aws::Client::ClientConfiguration clientConfig;
		if(!region.empty()) 
			clientConfig.region = region;
		Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

		std::cout << "Creating table " << table << 
            " with a simple primary key: \"Name\"" << std::endl;

		Aws::DynamoDB::Model::CreateTableRequest req;

		Aws::DynamoDB::Model::AttributeDefinition hk;
		hk.SetAttributeName("Name");
		hk.SetAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
		req.AddAttributeDefinitions(hk);

		Aws::DynamoDB::Model::KeySchemaElement keyscelt;
		keyscelt.WithAttributeName("Name").WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
		req.AddKeySchema(keyscelt);

		Aws::DynamoDB::Model::ProvisionedThroughput thruput;
		thruput.SetReadCapacityUnits(5);  
		thruput.SetWriteCapacityUnits(5);
		req.WithProvisionedThroughput(thruput);

		req.WithTableName(table);

		const Aws::DynamoDB::Model::CreateTableOutcome result = dynamoClient.CreateTable(req);
		if (result.IsSuccess()) {
			std::cout << "Table \"" << result.GetResult().GetTableDescription().GetTableName() << 
                 " was created!" << std::endl;
		} else {
			std::cout << "Failed to create table: " << result.GetError().GetMessage();
		}
	}
	Aws::ShutdownAPI(options);
	return 0;
}