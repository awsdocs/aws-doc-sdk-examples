/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0


Purpose

  This example verifies that you can connect to your AWS account and perform
  simple read operations on your AWS objects. This example reads the names
  of your Amazon RDS DB instances and your Amazon S3 buckets. If you don't
  have any DB instances or buckets, the example still connects to your AWS
  account and reports what it can't find.

  This code example was created by using Visual Studio 2019.
  This code example uses .NET Core 3.1 to create a cross-platform application.
  This code example creates a console application.


Prerequisites

To build and run this example, you need the following:

- The AWS SDK for .NET. For more information, see the AWS SDK for .NET
  Developer Guide (https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html).

- AWS credentials and a default AWS Region. If you have the AWS CLI installed,
  you can specify them in a local AWS config file such as C:\Users\username\.aws\config,
  and an AWS credentials file such as C:\Users\username\.aws\credentials. For
  more information, see the AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide (https://docs.aws.amazon.com/credref/latest/refdocs/overview.html).

- The AWSSDK.RDS package. This is already referenced in the solution.

- The AWSSDK.S3 package. This is already referenced in the solution.


Running the code

  1. Open the solution in Visual Studio.
  2. To build the solution, choose Build, Build Solution.
  3. To run the code, choose Debug, Start Debugging.


Running the tests

  1. Open the solution in Visual Studio.
  2. (Optional) To open the Test Explorer window, choose Test, Test Explorer.
  3. To run the tests, choose Test, Run All Tests.


Additional information

  - As an AWS best practice, grant this code least privilege, or only the permissions required to perform a task.
    For more information, see Grant Least Privilege (https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.

  - This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions.
    For more information, see Region Table (https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services) on the AWS website.

  - Running this code might result in charges to your AWS account.
*/


namespace Getting_Started_VS
{
    using System;
    using System.Threading.Tasks;


    public class Program
    {
        private static void Main(string[] args)
        {
            Console.WriteLine("Hello...");
            Console.WriteLine();

            try
            {
                Console.WriteLine("Checking your DB instances...");
                Task.Run(CheckRDSInstances).Wait();
            }
            catch (Exception e)
            {
                Console.WriteLine("Unable to check DB instances.  Error:");
                Console.WriteLine(e.Message);
            }

            try
            {
                Console.WriteLine();
                Console.WriteLine("Checking your S3 buckets...");
                Task.Run(CheckS3Buckets).Wait();
            }
            catch (Exception e)
            {
                Console.WriteLine("Unable to check S3 buckets.  Error:");
                Console.WriteLine(e.Message);
            }

            Console.WriteLine();
            Console.WriteLine("...goodbye.");
        } // main


        /*
        Purpose
          This task connects to your AWS account and reads the names of your DB instances
          This task looks for DB instances in one Region only

        Outputs
          This task writes the names of your DB instances to the console
        */
        public static async Task CheckRDSInstances()
        {
            Amazon.RDS.AmazonRDSClient client;
            Amazon.RDS.Model.DescribeDBInstancesRequest request;
            Amazon.RDS.Model.DescribeDBInstancesResponse response;

            client = new Amazon.RDS.AmazonRDSClient();
            request = new Amazon.RDS.Model.DescribeDBInstancesRequest();

            response = await client.DescribeDBInstancesAsync(request);

            if (response.DBInstances.Count > 0)
            {
                foreach (Amazon.RDS.Model.DBInstance i in response.DBInstances)
                {
                    Console.WriteLine(i.DBInstanceIdentifier);
                }
            }
            else
            {
                Console.WriteLine("You don't have any DB instances in this region.");
            }
        }


        /*
        Purpose
          This task connects to your AWS account and reads the names of your S3 buckets

        Outputs
          This task writes the names of your S3 buckets to the console
        */
        public static async Task CheckS3Buckets()
        {
            Amazon.S3.AmazonS3Client client;
            Amazon.S3.Model.ListBucketsRequest request;
            Amazon.S3.Model.ListBucketsResponse response;

            client = new Amazon.S3.AmazonS3Client();
            request = new Amazon.S3.Model.ListBucketsRequest();

            response = await client.ListBucketsAsync(request);

            if (response.Buckets.Count > 0)
            {
                foreach (Amazon.S3.Model.S3Bucket b in response.Buckets)
                {
                    Console.WriteLine(b.BucketName);
                }
            }
            else
            {
                Console.WriteLine("You don't have any S3 buckets.");
            }
        }
    }
}
