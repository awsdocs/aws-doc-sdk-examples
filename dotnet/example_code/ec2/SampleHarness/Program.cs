 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


﻿/*******************************************************************************
* Copyright 2009-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

using System;
using Amazon;
using Amazon.EC2;

namespace AWS.Samples
{

    public partial class Program
    {
        static void Main(string[] args)
        {
            AmazonEC2Client client = new AmazonEC2Client(RegionEndpoint.USWest2);

            Console.WriteLine("ec2Client is operating in the {0} region.", client.Config.RegionEndpoint.DisplayName);


            string keyPairName = "EC2-Sample-Keypair";

            CreateKeyPair(client, keyPairName, @"C:\\temp\\" + keyPairName + ".pem");

            EnumerateKeyPairs(client);


            EnumerateSecurityGroups(client);

            string securityGroupName = "default";


            DescribeImages(client);

            var instanceId = LaunchInstance( keyPairName, securityGroupName, client );

            TerminateInstance(client, instanceId);


            Console.WriteLine("Press any key to exit.");

            Console.ReadLine();
        }
    }
}
