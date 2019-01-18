// snippet-sourcedescription:[02_CreatingTable.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.02_CreatingTable] 

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
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {
    /*--------------------------------------------------------------------------
     *                       CreatingTable_async
     *--------------------------------------------------------------------------*/
    public static async Task CreatingTable_async( string  new_table_name,
                               List<AttributeDefinition>  table_attributes,
                               List<KeySchemaElement>     table_key_schema,
                               ProvisionedThroughput      provisionedThroughput )
    {
      Console.WriteLine( "  -- Creating a new table named {0}...", new_table_name );
      if( await checkingTableExistence_async( new_table_name ) )
      {
        Console.WriteLine( "     -- No need to create a new table..." );
        return;
      }
      if( operationFailed )
        return;

      operationSucceeded = false;
      Task<bool> newTbl = CreateNewTable_async( new_table_name,
                                                table_attributes,
                                                table_key_schema,
                                                provisionedThroughput );
      await newTbl;
    }


    /*--------------------------------------------------------------------------
     *                      checkingTableExistence_async
     *--------------------------------------------------------------------------*/
    static async Task<bool> checkingTableExistence_async( string tblNm )
    {
      DescribeTableResponse descResponse;

      operationSucceeded = false;
      operationFailed = false;
      ListTablesResponse tblResponse = await Ddb_Intro.client.ListTablesAsync();
      if (tblResponse.TableNames.Contains(tblNm))
      {
        Console.WriteLine("     A table named {0} already exists in DynamoDB!", tblNm);

        // If the table exists, get its description
        try
        {
          descResponse = await Ddb_Intro.client.DescribeTableAsync(Ddb_Intro.movies_table_name);
          operationSucceeded = true;
        }
        catch (Exception ex)
        {
          Console.WriteLine("     However, its description is not available ({0})", ex.Message);
          Ddb_Intro.moviesTableDescription = null;
          operationFailed = true;
          return ( true );
        }
        Ddb_Intro.moviesTableDescription = descResponse.Table;
        return ( true );
      }
      return ( false );
    }


    /*--------------------------------------------------------------------------
     *                CreateNewTable_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> CreateNewTable_async( string  table_name,
                                                         List<AttributeDefinition> table_attributes,
                                                         List<KeySchemaElement>    table_key_schema,
                                                         ProvisionedThroughput     provisioned_throughput )
    {
      CreateTableRequest  request;
      CreateTableResponse response;

      // Build the 'CreateTableRequest' structure for the new table
      request = new CreateTableRequest
      {
        TableName             = table_name,
        AttributeDefinitions  = table_attributes,
        KeySchema             = table_key_schema,
        // Provisioned-throughput settings are always required,
        // although the local test version of DynamoDB ignores them.
        ProvisionedThroughput = provisioned_throughput
      };

      operationSucceeded = false;
      operationFailed = false;
      try
      {
        Task<CreateTableResponse> makeTbl = Ddb_Intro.client.CreateTableAsync( request );
        response = await makeTbl;
        Console.WriteLine( "     -- Created the \"{0}\" table successfully!", table_name );
        operationSucceeded = true;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     FAILED to create the new table, because: {0}.", ex.Message );
        operationFailed = true;
        return( false );
      }

      // Report the status of the new table...
      Console.WriteLine( "     Status of the new table: '{0}'.", response.TableDescription.TableStatus );
      Ddb_Intro.moviesTableDescription = response.TableDescription;
      return ( true );
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.02_CreatingTable]