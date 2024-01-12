// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
 

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
