 
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

using Amazon.EC2;
using Amazon.EC2.Model;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AWS.Samples
{
    public partial class Program
    {

        public static void CreateKeyPair( AmazonEC2Client ec2Client,  string keyPairName,  string privateKeyFile )
        {
            var request = new CreateKeyPairRequest();
            request.KeyName = keyPairName;

            try
            {
                var response = ec2Client.CreateKeyPair(request);
                Console.WriteLine();
                Console.WriteLine("New key: " + keyPairName);

                // Save the private key in a .pem file
                using (FileStream s = new FileStream(privateKeyFile, FileMode.Create))
                using (StreamWriter writer = new StreamWriter(s))
                {
                    writer.WriteLine(response.KeyPair.KeyMaterial);
                }
            }
            catch (AmazonEC2Exception ex)
            {
                // Check the ErrorCode to see if the key already exists.
                if ("InvalidKeyPair.Duplicate" == ex.ErrorCode)
                {
                    Console.WriteLine("The key pair \"{0}\" already exists.", keyPairName);
                }
                else
                {
                    // The exception was thrown for another reason, so re-throw the exception.
                    throw;
                }
            }
        }

        public static void EnumerateKeyPairs(AmazonEC2Client ec2Client)
        {

            var request = new DescribeKeyPairsRequest();
            var response = ec2Client.DescribeKeyPairs(request);

            foreach (KeyPairInfo item in response.KeyPairs)
            {
                Console.WriteLine("Existing key pair: " + item.KeyName);
            }
        }
    }
}
