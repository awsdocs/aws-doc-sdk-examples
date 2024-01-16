// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
 

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.EC2;
using Amazon.EC2.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AWS.Samples
{
    public partial class Program
    {
        static void EnumerateSecurityGroups(AmazonEC2Client ec2Client)
        {
            var request = new DescribeSecurityGroupsRequest();
            var response = ec2Client.DescribeSecurityGroups(request);

            List<SecurityGroup> mySGs = response.SecurityGroups;

            foreach (SecurityGroup item in mySGs)
            {
                Console.WriteLine("Security group: ");
                Console.WriteLine("\tGroupId: "      + item.GroupId);
                Console.WriteLine("\tGroupName: "    + item.GroupName);
                Console.WriteLine("\tVpcId: "        + item.VpcId);
                Console.WriteLine("\tDescription: " + item.Description);

                Console.WriteLine();
            }
        }
    }
}
