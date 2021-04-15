// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.02_CreatingTable]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        public static async Task CreatingTable_async(string newTableName,
                                   List<AttributeDefinition> tableAttributes,
                                   List<KeySchemaElement> tableKeySchema,
                                   ProvisionedThroughput provisionedThroughput)
        {
            Console.WriteLine("  -- Creating a new table named {0}...", newTableName);

            if (await checkingTableExistence_async(newTableName))
            {
                Console.WriteLine("     -- No need to create a new table...");
                return;
            }

            if (OperationFailed)
                return;

            OperationSucceeded = false;
            Task<bool> newTbl = CreateNewTable_async(newTableName,
                                                      tableAttributes,
                                                      tableKeySchema,
                                                      provisionedThroughput);
            await newTbl;
        }

        static async Task<bool> checkingTableExistence_async(string tblNm)
        {
            DescribeTableResponse descResponse;

            OperationSucceeded = false;
            OperationFailed = false;
            ListTablesResponse tblResponse = await DdbIntro.Client.ListTablesAsync();

            if (tblResponse.TableNames.Contains(tblNm))
            {
                Console.WriteLine("     A table named {0} already exists in DynamoDB!", tblNm);

                // If the table exists, get its description
                try
                {
                    descResponse = await DdbIntro.Client.DescribeTableAsync(DdbIntro.MoviesTableName);
                    OperationSucceeded = true;
                }
                catch (Exception ex)
                {
                    Console.WriteLine("     However, its description is not available ({0})", ex.Message);
                    DdbIntro.MoviesTableDescription = null;
                    OperationFailed = true;
                    return (true);
                }

                DdbIntro.MoviesTableDescription = descResponse.Table;
                return (true);
            }

            return (false);
        }
        
        public static async Task<bool> CreateNewTable_async(string tableName,
                                                             List<AttributeDefinition> tableAttributes,
                                                             List<KeySchemaElement> tableKeySchema,
                                                             ProvisionedThroughput provisionedThroughput)
        {
            CreateTableRequest request;
            CreateTableResponse response;

            request = new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = tableAttributes,
                KeySchema = tableKeySchema,
                // Provisioned-throughput settings are always required,
                // although the local test version of DynamoDB ignores them.
                ProvisionedThroughput = provisionedThroughput
            };

            OperationSucceeded = false;
            OperationFailed = false;

            try
            {
                Task<CreateTableResponse> makeTbl = DdbIntro.Client.CreateTableAsync(request);
                response = await makeTbl;
                Console.WriteLine("     -- Created the \"{0}\" table successfully!", tableName);
                OperationSucceeded = true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("     FAILED to create the new table, because: {0}.", ex.Message);
                OperationFailed = true;
                return (false);
            }

            Console.WriteLine("     Status of the new table: '{0}'.", response.TableDescription.TableStatus);
            DdbIntro.MoviesTableDescription = response.TableDescription;
            return (true);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.02_CreatingTable]