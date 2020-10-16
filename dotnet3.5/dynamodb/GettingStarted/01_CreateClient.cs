// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.01_CreateClient]
using System;
using System.Net.Sockets;
using Amazon.DynamoDBv2;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        /*-----------------------------------------------------------------------------------
      *  If you are creating a client for the DynamoDB service, make sure your credentials
      *  are set up first, as explained in:
      *  https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SettingUp.DynamoWebService.html,
      *
      *  If you are creating a client for DynamoDBLocal (for testing purposes),
      *  DynamoDB-Local should be started first. For most simple testing, you can keep
      *  data in memory only, without writing anything to disk.  To do this, use the
      *  following command line:
      *
      *    java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -inMemory
      *
      *  For information about DynamoDBLocal, see:
      *  https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html.
      *-----------------------------------------------------------------------------------*/
        /*--------------------------------------------------------------------------
         *          createClient
         *--------------------------------------------------------------------------*/
        public static bool CreateClient(bool useDynamoDbLocal)
        {
            if (useDynamoDbLocal)
            {
                OperationSucceeded = false;
                OperationFailed = false;

                // First, check to see whether anyone is listening on the DynamoDB local port
                // (by default, this is port 8000, so if you are using a different port, modify this accordingly)
                bool localFound;

                try
                {
                    using (var tcpClient = new TcpClient())
                    {
                        var result = tcpClient.BeginConnect("localhost", 8000, null, null);
                        localFound = result.AsyncWaitHandle.WaitOne(3000); // Wait 3 seconds
                        tcpClient.EndConnect(result);
                    }
                }
                catch
                {
                    localFound = false;
                }

                if (!localFound)
                {
                    Console.WriteLine("\n      ERROR: DynamoDB Local does not appear to have been started..." +
                                      "\n        (checked port 8000)");
                    OperationFailed = true;
                    return (false);
                }

                // If DynamoDB-Local does seem to be running, so create a client
                Console.WriteLine("  -- Setting up a DynamoDB-Local client (DynamoDB Local seems to be running)");
                AmazonDynamoDBConfig ddbConfig = new AmazonDynamoDBConfig();
                ddbConfig.ServiceURL = "http://localhost:8000";

                try
                {
                    Client = new AmazonDynamoDBClient(ddbConfig);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("     FAILED to create a DynamoDBLocal client; " + ex.Message);
                    OperationFailed = true;
                    return false;
                }
            }
            else
            {
                try
                {
                    Client = new AmazonDynamoDBClient();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("     FAILED to create a DynamoDB client; " + ex.Message);
                    OperationFailed = true;
                }
            }

            OperationSucceeded = true;
            return true;
        }
    }
}
// snippet-end:[dynamodb.dotnet35.01_CreateClient]