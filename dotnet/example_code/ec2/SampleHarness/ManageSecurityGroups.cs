/*******************************************************************************
* Copyright 2009-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
