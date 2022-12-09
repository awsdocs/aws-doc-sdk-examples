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

//snippet-start:[dynamodb.cpp.put_item.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h> 
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/AttributeDefinition.h>
#include <aws/dynamodb/model/PutItemRequest.h>
#include <aws/dynamodb/model/PutItemResult.h>
#include <iostream>
//snippet-end:[dynamodb.cpp.put_item.inc]
#include "dynamodb_samples.h"

//snippet-start:[dynamodb.cpp.put_item.code]
//! Put an item in a DynamoDB table.
/*!
  \sa putItem()
  \param tableName: The table name.
  \param artistKey: The artist key. This is the partition key for the table.
  \param artistValue: The artist value.
  \param albumTitleKey: The album title key.
  \param albumTitleValue: The album title value.
  \param awardsKey: The awards key.
  \param awardsValue: The awards value.
  \param songTitleKey: The song title key.
  \param songTitleValue: The song title value.
  \param clientConfiguration: Aws client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::DynamoDB::putItem(const Aws::String& tableName,
             const Aws::String& artistKey,
             const Aws::String& artistValue,
             const Aws::String& albumTitleKey,
             const Aws::String& albumTitleValue,
             const Aws::String& awardsKey,
             const Aws::String& awardsValue,
             const Aws::String& songTitleKey,
             const Aws::String& songTitleValue,
             const Aws::Client::ClientConfiguration &clientConfiguration)
{
    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    Aws::DynamoDB::Model::PutItemRequest putItemRequest;

    putItemRequest.AddItem(artistKey, Aws::DynamoDB::Model::AttributeValue().SetS(artistValue)); // This is the hash key.
    putItemRequest.AddItem(albumTitleKey, Aws::DynamoDB::Model::AttributeValue().SetS(albumTitleValue));
    putItemRequest.AddItem(awardsKey, Aws::DynamoDB::Model::AttributeValue().SetS(awardsValue));
    putItemRequest.AddItem(songTitleKey, Aws::DynamoDB::Model::AttributeValue().SetS(songTitleValue));

    const Aws::DynamoDB::Model::PutItemOutcome outcome = dynamoClient.PutItem(putItemRequest);
    if (outcome.IsSuccess())
    {
        std::cout << "Successfully added Item!" << std::endl;
    }
    else {
        std::cerr << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

//snippet-end:[dynamodb.cpp.put_item.code]

/*
 *
 *  main function
 *
 *  Usage: 'run_put_item <table_name> <artist_key> <artist_value> <album_title_key>
 *     <album_title_value> <awards_key> <awards_value> <song_title_key> <song_title_value>'
 *
 *  Prerequisites: a DynamoDB table named <table_name> with <artist_key> for the partition key.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc < 9)
    {
       std::cout << R"(
Usage:
    <table_name> <artist_key> <artist_value> <album_title_key> <album_title_value> <awards_key>
        <awards_value> <song_title_key> <song_title_value>
Where:
    table_name - the Amazon DynamoDB table in which an item is placed (for example, Music3).
    artist_key - the key the artist used in the Amazon DynamoDB table. This is the partition key.
    artist_value - the value for the artist (for example, Famous Band).
    album_title_key - the key for the album title (for example, AlbumTitle).
    album_title_value - the value for the album title (for example, Songs About Life ).
    awards_key - the key for awards (for example, Awards).
    awards_value - the value of the awards (for example, 10).
    song_title_key - the key for the song title (for example, SongTitle).
    song_title_value - the value of the song title (for example, Happy Day).
**Warning** This program will  place an item that you specify into a table!;
)";
       return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String tableName = (argv[1]);
        const Aws::String artistKey = (argv[2]);
        const Aws::String artistValue = (argv[3]);
        const Aws::String albumTitleKey = (argv[4]);
        const Aws::String albumTitleValue = (argv[5]);
        const Aws::String awardsKey = (argv[6]);
        const Aws::String awardsValue = (argv[7]);
        const Aws::String songTitleKey = (argv[8]);
        const Aws::String songTitleValue = (argv[9]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::DynamoDB::putItem(tableName, artistKey, artistValue, albumTitleKey, albumTitleValue,
                                  awardsKey, awardsValue, songTitleKey, songTitleValue, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD