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
// snippet-start:[dynamodb.dotNET.CodeExample.01_CreateClient] 
using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;

namespace DynamoDB_intro
{
  public static partial class DdbIntro
  {
        /*-----------------------------------------------------------------------------------
          *  If you are creating a client for the Amazon DynamoDB service, make sure your credentials
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

        // So we know whether local DynamoDB is running
        private static readonly string Ip = "localhost";
        private static readonly int Port = 8000;
        private static readonly string EndpointUrl = "http://" + Ip + ":" + Port;

        private static bool IsPortInUse()
        {
            bool isAvailable = true;

            // Evaluate current system TCP connections. This is the same information provided
            // by the netstat command line application, just in .Net strongly-typed object
            // form.  We will look through the list, and if our port we would like to use
            // in our TcpClient is occupied, we will set isAvailable to false.
            IPGlobalProperties ipGlobalProperties = IPGlobalProperties.GetIPGlobalProperties();
            IPEndPoint[] tcpConnInfoArray = ipGlobalProperties.GetActiveTcpListeners();

            foreach (IPEndPoint endpoint in tcpConnInfoArray)
            {
                if (endpoint.Port == Port)
                {
                    isAvailable = false;
                    break;
                }
            }

            return isAvailable;
        }
       
    public static bool createClient(bool useDynamoDbLocal)
    {
      if (useDynamoDbLocal)
      {
        // First, check to see whether anyone is listening on the DynamoDB local port
        // (by default, this is port 8000, so if you are using a different port, modify this accordingly)
        var portUsed = IsPortInUse();

        if (portUsed)
        {
            Console.WriteLine("The local version of DynamoDB is NOT running.");
            return (false);
        }

        // DynamoDB-Local is running, so create a client
        Console.WriteLine( "  -- Setting up a DynamoDB-Local client (DynamoDB Local seems to be running)" );
        AmazonDynamoDBConfig ddbConfig = new AmazonDynamoDBConfig();
        ddbConfig.ServiceURL = EndpointUrl;
        try
        {
            Client = new AmazonDynamoDBClient(ddbConfig);
        }
        catch( Exception ex )
        {
          Console.WriteLine( "     FAILED to create a DynamoDBLocal client; " + ex.Message );
          return false;
        }
      }
      else
      {
          Client = new AmazonDynamoDBClient();
      }
     
      return true;
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.01_CreateClient]