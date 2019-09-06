 
//snippet-sourcedescription:[batch_get_item.cpp demonstrates how to batch get items from different Amazon DynamoDB tables.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


// BatchGetItem.cpp : Defines the entry point for the console application.
//
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/BatchGetItemRequest.h>
#include <aws/dynamodb/model/KeysAndAttributes.h>
#include <aws/core/http/HttpRequest.h>
#include <iostream>


/**
* Batch get items from different DynamoDB tables.
*
* Sample data and loading instructions can be found at:
* https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SampleData.html
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
	Aws::SDKOptions options;

	Aws::InitAPI(options);
	{
		Aws::Client::ClientConfiguration clientConfig;
		// Set the region where your DynamoDB tables exist
		clientConfig.region = Aws::Region::US_WEST_2;
		Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

		Aws::DynamoDB::Model::BatchGetItemRequest req;

		// Table1: Forum
		Aws::String t1Name = "Forum";
		Aws::DynamoDB::Model::KeysAndAttributes t1KeyAttrs;
		//Table1: Projection expression
		t1KeyAttrs.SetProjectionExpression("#n, Category, Messages, #v");

		// Table1: Expression attribute names
		Aws::Http::HeaderValueCollection hvc;
		hvc.emplace("#n", "Name");
		hvc.emplace("#v", "Views");
		t1KeyAttrs.SetExpressionAttributeNames(hvc);

		// Table1: Set key name, type and value to search
		//
		// Since we are searching for two possible values of "Name",
		// we have to use to separate key maps
		//
		// Name = "Amazon DynamoDB"
		Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> t1KeysA;
		Aws::DynamoDB::Model::AttributeValue t1key1;
		t1key1.SetS("Amazon DynamoDB");
		t1KeysA.emplace("Name", t1key1);
		t1KeyAttrs.AddKeys(t1KeysA);

		// Name = "Amazon S3"
		Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> t1KeysB;
		Aws::DynamoDB::Model::AttributeValue t1key2;
		t1key2.SetS("Amazon S3");
		t1KeysB.emplace("Name", t1key2);
		t1KeyAttrs.AddKeys(t1KeysB);
		req.AddRequestItems(t1Name, t1KeyAttrs);

		// Table2: ProductCatalog
		Aws::String t2Name = "ProductCatalog";
		Aws::DynamoDB::Model::KeysAndAttributes t2KeyAttrs;
		//Table2: Projection expression
		t1KeyAttrs.SetProjectionExpression("Title, Price, Color");

		// Table2: Set key name, type and value to search
		//
		// Name = "Id", value = 201
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
}