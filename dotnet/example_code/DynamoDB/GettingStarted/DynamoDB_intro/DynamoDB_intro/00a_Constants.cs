// snippet-sourcedescription:[00a_Constants.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.161099da-848c-4ddc-b833-e3bc18ae8609] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
/*******************************************************************************
* Copyright 2009-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/
using System.Collections.Generic;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
  public partial class Ddb_Intro
  {
    /*==========================================================================
     *      Constant/Static Values Used by this introductory sample
     *==========================================================================*/
    public const string commaSep = ", ";
    public const string stepString =
      "\n--------------------------------------------------------------------------------------" +
      "\n    STEP {0}:  {1}" +
      "\n--------------------------------------------------------------------------------------";

    /*---------------------------------------------------------
     *    1.  The data used to create a new table
     *---------------------------------------------------------*/
    // movies_table_name
    public const string movies_table_name = "Movies";

    // key names for the Movies table
    public const string partition_key_name = "year";
    public const string sort_key_name      = "title";

    // movie_items_attributes
    public static List<AttributeDefinition> movie_items_attributes
      = new List<AttributeDefinition>
    {
      new AttributeDefinition
      {
        AttributeName = partition_key_name,
        AttributeType = "N"
      },
      new AttributeDefinition
      {
        AttributeName = sort_key_name,
        AttributeType = "S"
      }
    };

    // movies_key_schema
    public static List<KeySchemaElement> movies_key_schema
      = new List<KeySchemaElement>
    {
      new KeySchemaElement
      {
        AttributeName = partition_key_name,
        KeyType = "HASH"
      },
      new KeySchemaElement
      {
        AttributeName = sort_key_name,
        KeyType = "RANGE"
      }
    };

    // movies_table_provisioned_throughput
    public static ProvisionedThroughput movies_table_provisioned_throughput
      = new ProvisionedThroughput( 1, 1 );


    /*---------------------------------------------------------
     *    2.  The path to the JSON movies data file to load
     *---------------------------------------------------------*/
    public const string movieDataPath = "./moviedata.json";
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.161099da-848c-4ddc-b833-e3bc18ae8609]