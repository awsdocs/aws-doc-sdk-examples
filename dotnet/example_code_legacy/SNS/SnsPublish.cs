// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
/* Build 
   Don't forget to use the Visual Studio command prompt
   Tested with csc /version == 2.10.0.0
     (C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\MSBuild\15.0\bin\Roslyn)
   AWSSDK.Core.dll
     (C:\Users\USERNAME\.nuget\packages\awssdk.core\3.3.100\lib\net45)
   AWSSDK.SimpleNotificationService.dll
     (C:\Users\USERNAME\.nuget\packages\awssdk.simplenotificationservice\3.3.0.11\lib\net45)

   csc SnsPublish.cs -reference:AWSSDK.Core.dll -reference:AWSSDK.SimpleNotificationService.dll

   Get the list of AWSSDK Nuget packages at:
       https://www.nuget.org/packages?q=AWSSDK&prerel=false
 */
// snippet-start:[sns.dotnet.publish]
using System;
using System.Linq;
using System.Threading.Tasks;
using Amazon;
using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;

namespace SnsPublish
{
    class Program
    {
        static void Main(string[] args)
        {
            // US phone numbers must be in the correct format:
            // +1 (nnn) nnn-nnnn OR +1nnnnnnnnnn
            string number = args[0];
            string message = "Hello at " + DateTime.Now.ToShortTimeString();

            var client = new AmazonSimpleNotificationServiceClient(region: Amazon.RegionEndpoint.USWest2);
            var request = new PublishRequest
            {
                Message = message,
                PhoneNumber = number
            };

            try
            {
                var response = client.Publish(request);

                Console.WriteLine("Message sent to " + number + ":");
                Console.WriteLine(message);
            }
            catch (Exception ex)
            {
                Console.WriteLine("Caught exception publishing request:");
                Console.WriteLine(ex.Message);
            }
        }
    }
}
// snippet-end:[sns.dotnet.publish]