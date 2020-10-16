// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.07_DeletingItem]
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        /*--------------------------------------------------------------------------
         *                       DeletingItem_async
         *--------------------------------------------------------------------------*/
        public static async Task<bool> DeletingItem_async(Table table, int year, string title,
            Expression condition = null)
        {
            Document deletedItem;
            OperationSucceeded = false;
            OperationFailed = false;

            // Create Primitives for the HASH and RANGE portions of the primary key
            Primitive hash = new Primitive(year.ToString(), true);
            Primitive range = new Primitive(title, false);
            DeleteItemOperationConfig deleteConfig = new DeleteItemOperationConfig();
            deleteConfig.ConditionalExpression = condition;
            deleteConfig.ReturnValues = ReturnValues.AllOldAttributes;

            Console.WriteLine("  -- Trying to delete the {0} movie \"{1}\"...", year, title);

            try
            {
                Task<Document> delItem = table.DeleteItemAsync(hash, range, deleteConfig);
                deletedItem = await delItem;
            }
            catch (Exception ex)
            {
                Console.WriteLine("     FAILED to delete the movie item, for this reason:\n       {0}\n", ex.Message);
                OperationFailed = true;
                return (false);
            }

            Console.WriteLine("     -- SUCCEEDED in deleting the movie record that looks like this:\n" +
                              deletedItem.ToJsonPretty());
            OperationSucceeded = true;
            return (true);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.07_DeletingItem]