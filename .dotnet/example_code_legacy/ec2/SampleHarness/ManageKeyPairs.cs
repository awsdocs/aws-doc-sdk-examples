// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
 

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
