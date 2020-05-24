// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.01_CreateClient] 

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
using System.Net.Sockets;
using Amazon.DynamoDBv2;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
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
    public static bool createClient( bool useDynamoDBLocal )
    {
      if( useDynamoDBLocal )
      {
        operationSucceeded = false;
        operationFailed = false;

        // First, check to see whether anyone is listening on the DynamoDB local port
        // (by default, this is port 8000, so if you are using a different port, modify this accordingly)
        bool localFound = false;
        try
        {
          using (var tcp_client = new TcpClient())
          {
            var result = tcp_client.BeginConnect("localhost", 8000, null, null);
            localFound = result.AsyncWaitHandle.WaitOne(3000); // Wait 3 seconds
            tcp_client.EndConnect(result);
          }
        }
        catch
        {
          localFound =  false;
        }
        if( !localFound )
        {
          Console.WriteLine("\n      ERROR: DynamoDB Local does not appear to have been started..." +
                            "\n        (checked port 8000)");
          operationFailed = true;
          return (false);
        }

      // If DynamoDB-Local does seem to be running, so create a client
        Console.WriteLine( "  -- Setting up a DynamoDB-Local client (DynamoDB Local seems to be running)" );
        AmazonDynamoDBConfig ddbConfig = new AmazonDynamoDBConfig();
        ddbConfig.ServiceURL = "http://localhost:8000";
        try { client = new AmazonDynamoDBClient( ddbConfig ); }
        catch( Exception ex )
        {
          Console.WriteLine( "     FAILED to create a DynamoDBLocal client; " + ex.Message );
          operationFailed = true;
          return false;
        }
      }

      else
      {
        try { client = new AmazonDynamoDBClient( ); }
        catch( Exception ex )
        {
          Console.WriteLine( "     FAILED to create a DynamoDB client; " + ex.Message );
          operationFailed = true;
        }
      }
      operationSucceeded = true;
      return true;
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.01_CreateClient]