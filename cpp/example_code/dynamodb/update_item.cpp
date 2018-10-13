//snippet-sourceauthor: [tapasweni-pathak]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
#include <aws/dynamodb/model/UpdateItemRequest.h>
#include <aws/dynamodb/model/UpdateItemResult.h>
#include <iostream>


/**
* Update a DynamoDB item in a table.
*
* Takes the name of the table, an item to update (primary key value), and the
* greeting to update it with.
*
* The primary key used is "Name", and the greeting will be added to the
* "Greeting" field.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/credentials.html
*/
int main(int argc, char** argv)
{
    const std::string USAGE = "\n" \
        "Usage:\n"
        "    update_item <table> <name> <fld:val> ..\n\n"
        "Where:\n"
        "    table    - the table to put the item in.\n"
        "    name     - a name to update\n"
        "    fld:val  - field name:new updated value pairs\n\n"
        "Additional fields can be specified by appending them to the end of the\n"
        "input.\n\n"
        "Examples:\n"
        "    update_item SiteColors text default:000000 bold:b22222\n"
        "    update_item SiteColors background default:eeeeee code:d3d3d3\n\n";

    if (argc < 3)
    {
        std::cout << USAGE;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        const Aws::String table(argv[1]);
        const Aws::String name(argv[2]);

        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        Aws::DynamoDB::Model::UpdateItemRequest uir;
        uir.SetTableName(table);

        Aws::DynamoDB::Model::AttributeValue av;
        av.SetS(name);
        uir.AddKey("Name", av);

        for (int x = 3; x < argc; x++)
        {
            const Aws::String arg(argv[x]);
            const Aws::Vector<Aws::String>& flds = Aws::Utils::StringUtils::Split(arg, ':');
            if (flds.size() == 2)
            {
                Aws::DynamoDB::Model::AttributeValue val;
                val.SetS(flds[1]);
                Aws::DynamoDB::Model::AttributeValueUpdate avu;
                avu.SetValue(val);
                uir.AddAttributeUpdates(flds[0], avu);
            }
            else
            {
                std::cout << "Invalid argument: " << arg << std::endl << USAGE;
                return 1;
            }
        }

        const Aws::DynamoDB::Model::UpdateItemOutcome& result = dynamoClient.UpdateItem(uir);
        if (!result.IsSuccess())
        {
            std::cout << result.GetError().GetMessage() << std::endl;
            return 1;
        }
        std::cout << "Done!" << std::endl;
    }
    Aws::ShutdownAPI(options);
    return 0;
}