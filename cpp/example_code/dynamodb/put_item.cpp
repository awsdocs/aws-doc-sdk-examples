//snippet-sourcedescription:[put_item.cpp demonstrates how to put an item into an Amazon DynamoDB table.]
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

//snippet-start:[dynamodb.cpp.put_item.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/PutItemRequest.h>
#include <aws/dynamodb/model/PutItemResult.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.put_item.inc]


/*
   Put an item in a DynamoDB table.

   To run this C++ code example, ensure that you have setup your development environment, including your credentials.
   For information, see this documentation topic:
   https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*/

int main(int argc, char** argv)
{
    const std::string USAGE = \
        "Usage:\n" 
        "    <tableName> <key> <keyVal> <albumtitle> <albumtitleval> <awards> <awardsval> <Songtitle> <songtitleval>\n\n" 
        "Where:\n" 
        "    tableName - the Amazon DynamoDB table in which an item is placed (for example, Music3).\n" 
        "    key - the key used in the Amazon DynamoDB table (for example, Artist).\n" 
        "    keyval - the key value that represents the item to get (for example, Famous Band).\n" 
        "    albumTitle - album title (for example, AlbumTitle).\n" 
        "    AlbumTitleValue - the name of the album (for example, Songs About Life ).\n" 
        "    Awards - the awards column (for example, Awards).\n" 
        "    AwardVal - the value of the awards (for example, 10).\n" 
        "    SongTitle - the song title (for example, SongTitle).\n" 
        "    SongTitleVal - the value of the song title (for example, Happy Day).\n" 
        "**Warning** This program will  place an item that you specify into a table!\n";

    if (argc < 9)
    {
       std::cout << USAGE;
       return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String table = (argv[1]);
        const Aws::String key = (argv[2]);
        const Aws::String keyVal = (argv[3]);
        const Aws::String albumTitle = (argv[4]);
        const Aws::String AlbumTitleValue = (argv[5]);
        const Aws::String Awards = (argv[6]);
        const Aws::String AwardVal = (argv[7]);
        const Aws::String SongTitle = (argv[8]);
        const Aws::String SongTitleVal = (argv[9]);

        // snippet-start:[dynamodb.cpp.put_item.code]
        Aws::Client::ClientConfiguration clientConfig;
        Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfig);

        Aws::DynamoDB::Model::PutItemRequest putItemRequest;
        putItemRequest.SetTableName(table);
               
        Aws::DynamoDB::Model::AttributeValue av;
        av.SetS(keyVal);
        
        Aws::DynamoDB::Model::AttributeValue album;
        album.SetS(AlbumTitleValue);

        Aws::DynamoDB::Model::AttributeValue awards;
        awards.SetS(AwardVal);

        Aws::DynamoDB::Model::AttributeValue song;
        song.SetS(SongTitleVal);

        // Add all AttributeValue objects.
        putItemRequest.AddItem(key, av);
        putItemRequest.AddItem(albumTitle, album);
        putItemRequest.AddItem(Awards, awards);
        putItemRequest.AddItem(SongTitle, song);

        const Aws::DynamoDB::Model::PutItemOutcome result = dynamoClient.PutItem(putItemRequest);
        if (!result.IsSuccess())
        {
            std::cout << result.GetError().GetMessage() << std::endl;
            return 1;
        }
        std::cout << "Successfully added Item!" << std::endl;
        // snippet-end:[dynamodb.cpp.put_item.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}