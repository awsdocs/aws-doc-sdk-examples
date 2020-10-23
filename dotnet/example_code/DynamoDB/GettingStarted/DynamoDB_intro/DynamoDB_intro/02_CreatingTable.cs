// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.02_CreatingTable] 
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
    public static partial class DdbIntro
    {
        public static async Task<bool> CheckingTableExistence_async(string tblNm)
        {
            var response = await DdbIntro.Client.ListTablesAsync();
            return response.TableNames.Contains(tblNm);
        }

        public static async Task<bool> CreateTable_async(string tableName,
            List<AttributeDefinition> tableAttributes,
            List<KeySchemaElement> tableKeySchema,
            ProvisionedThroughput provisionedThroughput)
        {
            bool response = true;

            // Build the 'CreateTableRequest' structure for the new table
            var request = new CreateTableRequest
            {
                TableName = tableName,
                AttributeDefinitions = tableAttributes,
                KeySchema = tableKeySchema,
                // Provisioned-throughput settings are always required,
                // although the local test version of DynamoDB ignores them.
                ProvisionedThroughput = provisionedThroughput
            };

            try
            {
                var makeTbl = await DdbIntro.Client.CreateTableAsync(request);
            }
            catch (Exception)
            {
                response = false;
            }

            return response;
        }

        public static async Task<TableDescription> GetTableDescription(string tableName)
        {
            TableDescription result = null;

            // If the table exists, get its description.
            try
            {
                var response = await DdbIntro.Client.DescribeTableAsync(tableName);
                result = response.Table;
            }
            catch (Exception)
            {}

            return result;
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.02_CreatingTable]