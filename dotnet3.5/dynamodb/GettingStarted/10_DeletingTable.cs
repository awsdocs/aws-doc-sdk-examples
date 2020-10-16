// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.10_DeletingTable]
using System;
using System.Threading.Tasks;

namespace GettingStarted
{ public static partial class DdbIntro
    {
        /*--------------------------------------------------------------------------
         *                DeletingTable_async
         *--------------------------------------------------------------------------*/
        public static async Task<bool> DeletingTable_async(string tableName)
        {
            OperationSucceeded = false;
            OperationFailed = false;

            Console.WriteLine("  -- Trying to delete the table named \"{0}\"...", tableName);
            Pause();
            Task tblDelete = Client.DeleteTableAsync(tableName);

            try
            {
                await tblDelete;
            }
            catch (Exception ex)
            {
                Console.WriteLine("     ERROR: Failed to delete the table, because:\n            " + ex.Message);
                OperationFailed = true;
                return (false);
            }

            Console.WriteLine("     -- Successfully deleted the table!");
            OperationSucceeded = true;
            Pause();
            return (true);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.10_DeletingTable]