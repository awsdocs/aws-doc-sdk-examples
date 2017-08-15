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
* Create a DynamoDB table.
*
* Takes the name of the table to create. The table will contain a 
* composite key, "Language" (hash) and "Greeting" (range).
*
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
	const Aws::String USAGE = "\n" \
		"Usage:\n"
		"    create_table_composite_key <table> <optional:region>\n\n"
		"Where:\n"
		"    table - the table to create\n"
		"    region- optional region\n\n"
		"Example:\n"
		"    create_table_composite_key HelloTable us-west-2\n";

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
			" with a composite primary key:\n" \
			"* Language - partition key\n" \
			"* Greeting - sort key\n";

		Aws::DynamoDB::Model::CreateTableRequest req;

		Aws::Vector<Aws::DynamoDB::Model::AttributeDefinition> attrs;
		Aws::DynamoDB::Model::AttributeDefinition hk1, hk2;
		hk1.WithAttributeName("Language").WithAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
		attrs.push_back(hk1);
		hk2.WithAttributeName("Greeting").WithAttributeType(Aws::DynamoDB::Model::ScalarAttributeType::S);
		attrs.push_back(hk2);
		req.SetAttributeDefinitions(attrs);

		Aws::Vector<Aws::DynamoDB::Model::KeySchemaElement> keyschs;
		Aws::DynamoDB::Model::KeySchemaElement kse1, kse2;
		kse1.WithAttributeName("Language").WithKeyType(Aws::DynamoDB::Model::KeyType::HASH);
		keyschs.push_back(kse1);
		kse2.WithAttributeName("Greeting").WithKeyType(Aws::DynamoDB::Model::KeyType::RANGE);
		keyschs.push_back(kse2);
		req.SetKeySchema(keyschs);

		Aws::DynamoDB::Model::ProvisionedThroughput thruput;
		thruput.WithReadCapacityUnits(5);  
		thruput.WithWriteCapacityUnits(5);
		req.WithProvisionedThroughput(thruput);

		req.SetTableName(table);

		const Aws::DynamoDB::Model::CreateTableOutcome result = dynamoClient.CreateTable(req);
		if (result.IsSuccess()) {
			std::cout << "Table \"" << result.GetResult().GetTableDescription().GetTableName() << 
				"\" was created!\n";
        } else {
			std::cout << "Failed to create table:" << result.GetError().GetMessage();
		}
	}
	Aws::ShutdownAPI(options);
	return 0;
}