//snippet-sourcedescription:[batch_get_item.cpp demonstrates how to batch get items from different Amazon DynamoDB tables.]
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


//snippet-start:[dynamodb.cpp.get_item_batch.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/BatchGetItemRequest.h>
#include <aws/dynamodb/model/KeysAndAttributes.h>
#include <aws/core/http/HttpRequest.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.get_item_batch.inc]


/**
  Batch get items from different Amazon DynamoDB tables.

   Sample data and loading instructions can be found at:
   https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SampleData.html

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
	Aws::SDKOptions options;

	//snippet-start:[dynamodb.cpp.get_item_batch.code]
	Aws::InitAPI(options);
	{
		Aws::Client::ClientConfiguration clientConfig;
		
		// Set the AWS Region where your DynamoDB tables exist.
		clientConfig.region = Aws::Region::US_WEST_2;
		Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

		Aws::DynamoDB::Model::BatchGetItemRequest req;

		// Table1: Forum.
		Aws::String t1Name = "Forum";
		Aws::DynamoDB::Model::KeysAndAttributes t1KeyAttrs;
		
		//Table1: Projection expression.
		t1KeyAttrs.SetProjectionExpression("#n, Category, Messages, #v");

		// Table1: Expression attribute names.
		Aws::Http::HeaderValueCollection hvc;
		hvc.emplace("#n", "Name");
		hvc.emplace("#v", "Views");
		t1KeyAttrs.SetExpressionAttributeNames(hvc);

		// Table1: Set key name, type and value to search.
		Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> t1KeysA;
		Aws::DynamoDB::Model::AttributeValue t1key1;
		t1key1.SetS("Amazon DynamoDB");
		t1KeysA.emplace("Name", t1key1);
		t1KeyAttrs.AddKeys(t1KeysA);

		Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> t1KeysB;
		Aws::DynamoDB::Model::AttributeValue t1key2;
		t1key2.SetS("Amazon S3");
		t1KeysB.emplace("Name", t1key2);
		t1KeyAttrs.AddKeys(t1KeysB);
		req.AddRequestItems(t1Name, t1KeyAttrs);

		// Table2: ProductCatalog.
		Aws::String t2Name = "ProductCatalog";
		Aws::DynamoDB::Model::KeysAndAttributes t2KeyAttrs;
		t2KeyAttrs.SetProjectionExpression("Title, Price, Color");

		// Table2: Set key name, type and value to search.
		Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> t2KeysA;
		Aws::DynamoDB::Model::AttributeValue t2key1;
		t2key1.SetN("201");
		t2KeysA.emplace("Id", t2key1);
		t2KeyAttrs.AddKeys(t2KeysA);
		req.AddRequestItems(t2Name, t2KeyAttrs);

		const Aws::DynamoDB::Model::BatchGetItemOutcome& result = dynamoClient.BatchGetItem(req);

		if (result.IsSuccess())
		{
			for(const auto& var : result.GetResult().GetResponses())
			{
				Aws::String tableName = var.first;
				std::cout << tableName << std::endl;
				if (tableName == "Forum")
				{
					std::cout << "Name | Category | Message | Views" << std::endl;
					for (const auto& itm : var.second)
					{
						std::cout << itm.at("Name").GetS() << " | ";
						std::cout << itm.at("Category").GetS() << " | ";
						std::cout << (itm.count("Message") == 0 ? "" : itm.at("Messages").GetN()) << " | ";
						std::cout << (itm.count("Views") == 0 ? "" : itm.at("Views").GetN()) << std::endl;
					}
				}
				else
				{
					std::cout << "Title | Price | Color" << std::endl;
					for (const auto& itm : var.second)
					{
						std::cout << itm.at("Title").GetS() << " | ";
						std::cout << (itm.count("Price") == 0 ? "" : itm.at("Price").GetN());
						if (itm.count("Color"))
						{
							std::wcout << " | ";
							for (const auto& litm : itm.at("Color").GetL())
								std::cout << litm->GetS() << " ";
						}
						std::wcout << std::endl;
					}
				}
			}
		}
		else
		{
			std::cout << "Batch get item failed: " << result.GetError().GetMessage();
		}
	}
	Aws::ShutdownAPI(options);
	return 0;
	//snippet-end:[dynamodb.cpp.get_item_batch.code]
}
