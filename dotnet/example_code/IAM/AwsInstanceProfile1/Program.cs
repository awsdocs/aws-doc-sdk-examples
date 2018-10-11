 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Identity and Access Management (IAM)]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


﻿/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
using System.IO;
using System.Collections.Generic;
using System.Threading;

using Amazon.Auth.AccessControlPolicy;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;
using Amazon.EC2;
using Amazon.EC2.Model;
using Amazon.EC2.Util;


namespace AwsInstanceProfile1
{

    class Program
    {

        public const string S3_READONLY_POLICY =
                                "{" +
                                "	\"Statement\" : [{" +
                                "			\"Action\" : [\"s3:Get*\"]," +
                                "			\"Effect\" : \"Allow\"," +
                                "			\"Resource\" : \"*\"" +
                                "		}" +
                                "	]" +
                                "}";

        static void Main(string[] args)
        {
            // Create an EC2 Client for use in examples
            AmazonEC2Client ec2Client = new AmazonEC2Client();
        }
        public static void CreateDemoUser()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var request = new CreateUserRequest
            {
                UserName = "DemoUser"
            };

            try
            {
                var response = client.CreateUser(request);

                Console.WriteLine("User Name = '{0}', ARN = '{1}'",
                  response.User.UserName, response.User.Arn);
            }
            catch (EntityAlreadyExistsException)
            {
                Console.WriteLine("User 'DemoUser' already exists.");
            }

        }
        public static User CreateReadOnlyUser()
        {
            var iamClient = new AmazonIdentityManagementServiceClient();
            try
            {
                // Create the IAM user
                var readOnlyUser = iamClient.CreateUser(new CreateUserRequest
                {
                    UserName = "S3UserReadOnlyAccess"
                }).User;

                // Assign the read only policy to the new user
                iamClient.PutUserPolicy(new PutUserPolicyRequest
                {
                    UserName = readOnlyUser.UserName,
                    PolicyName = "S3ReadOnlyAccess",
                    PolicyDocument = S3_READONLY_POLICY
                });
                return readOnlyUser;
            }
            catch (EntityAlreadyExistsException e)
            {
                Console.WriteLine(e.Message);
                var request = new GetUserRequest()
                {
                    UserName = "S3UserReadOnlyAccess"
                };

                return iamClient.GetUser(request).User;

            }
        }
        public static void CreateAccessKey()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                // Create an access key for the IAM user that can be used by the SDK
                var accessKey = iamClient.CreateAccessKey(new CreateAccessKeyRequest
                {
                    // Use the user we created in the CreateUser example
                    UserName = "S3UserReadOnlyAccess"
                }).AccessKey;

            }
            catch (LimitExceededException e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static User CreateUser()
        {
            var iamClient = new AmazonIdentityManagementServiceClient();
            try
            {
                // Create the IAM user
                var readOnlyUser = iamClient.CreateUser(new CreateUserRequest
                {
                    UserName = "S3UserReadOnlyAccess"
                }).User;

                // Assign the read only policy to the new user
                iamClient.PutUserPolicy(new PutUserPolicyRequest
                {
                    UserName = readOnlyUser.UserName,
                    PolicyName = "S3ReadOnlyAccess",
                    PolicyDocument = S3_READONLY_POLICY
                });
                return readOnlyUser;
            }
            catch (EntityAlreadyExistsException e)
            {
                Console.WriteLine(e.Message);
                var request = new GetUserRequest()
                {
                    UserName = "S3UserReadOnlyAccess"
                };

                return iamClient.GetUser(request).User;

            }
        }
        public static void ListAccessKeys()
        {

            var iamClient = new AmazonIdentityManagementServiceClient();
            var requestAccessKeys = new ListAccessKeysRequest
            {
                // Use the user created in the CreateAccessKey example
                UserName = "S3UserReadOnlyAccess",
                MaxItems = 10
            };
            var responseAccessKeys = iamClient.ListAccessKeys(requestAccessKeys);
            Console.WriteLine("  Access keys:");

            foreach (var accessKey in responseAccessKeys.AccessKeyMetadata)
            {
                Console.WriteLine("    {0}", accessKey.AccessKeyId);
                GetAccessKeyLastUsedRequest request = new GetAccessKeyLastUsedRequest()
                { AccessKeyId = accessKey.AccessKeyId };
                var response = iamClient.GetAccessKeyLastUsed(request);
                Console.WriteLine("Key last used " + response.AccessKeyLastUsed.LastUsedDate.ToLongDateString());
            }
        }
        public static void GetAccessKeysLastUsed()
        {

            var iamClient = new AmazonIdentityManagementServiceClient();
            var requestAccessKeys = new ListAccessKeysRequest
            {
                // Use the user we created in the CreateUser example
                UserName = "S3UserReadOnlyAccess"
            };
            var responseAccessKeys = iamClient.ListAccessKeys(requestAccessKeys);
            Console.WriteLine("  Access keys:");

            foreach (var accessKey in responseAccessKeys.AccessKeyMetadata)
            {
                Console.WriteLine("    {0}", accessKey.AccessKeyId);
                GetAccessKeyLastUsedRequest request = new GetAccessKeyLastUsedRequest()
                { AccessKeyId = accessKey.AccessKeyId };
                var response = iamClient.GetAccessKeyLastUsed(request);
                Console.WriteLine("Key last used " + response.AccessKeyLastUsed.LastUsedDate.ToLongDateString());
            }
        }
        public static void DeleteAccessKeys()
        {
            // Delete all the access keys we created for the Managing IAM Access Keys examples.
            var iamClient = new AmazonIdentityManagementServiceClient();
            var requestAccessKeys = new ListAccessKeysRequest
            {
                // Use the user we created in the CreateUser example
                UserName = "S3UserReadOnlyAccess"
            };
            var responseAccessKeys = iamClient.ListAccessKeys(requestAccessKeys);
            Console.WriteLine("  Access keys:");

            foreach (var accessKey in responseAccessKeys.AccessKeyMetadata)
            {
                Console.WriteLine("    {0}", accessKey.AccessKeyId);
                iamClient.DeleteAccessKey(new DeleteAccessKeyRequest
                {
                    UserName = "S3UserReadOnlyAccess",
                    AccessKeyId = accessKey.AccessKeyId
                });
                Console.WriteLine("Access Key " + accessKey.AccessKeyId + " deleted");
            }

        }
        public static void DeleteUser(User user)
        {
            var client = new AmazonIdentityManagementServiceClient();
            var request = new DeleteUserRequest()
            {
                UserName = user.UserName
            };

            try
            {
                var response = client.DeleteUser(request);

            }
            catch (NoSuchEntityException)
            {
                Console.WriteLine("User DemoUser' does not exist.");
            }
        }
        public static void UpdateKeyStatus()
        {
            // This example changes the status of the key specified by it's index in the list of access keys.
            // Optionally, you could change the keynumber parameter to be an AccessKey Id.
            var iamClient = new AmazonIdentityManagementServiceClient();
            var requestAccessKeys = new ListAccessKeysRequest
            {
                UserName = "S3UserReadOnlyAccess"
            };
            var responseAccessKeys = iamClient.ListAccessKeys(requestAccessKeys);
            UpdateAccessKeyRequest updateRequest = new UpdateAccessKeyRequest
            {
                UserName = "S3UserReadOnlyAccess",
                AccessKeyId = responseAccessKeys.AccessKeyMetadata[0].AccessKeyId,
                Status = Amazon.IdentityManagement.StatusType.Active
            };
            iamClient.UpdateAccessKey(updateRequest);
            Console.WriteLine("  Access key " + updateRequest.AccessKeyId + " updated");
        }
        public static void ListCertificates()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new ListServerCertificatesRequest();
                var response = iamClient.ListServerCertificates(request);
                foreach (KeyValuePair<string, string> kvp in response.ResponseMetadata.Metadata)
                {
                    Console.WriteLine("Key = {0}, Value = {1}",
                        kvp.Key, kvp.Value);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static void GetCertificate()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new GetServerCertificateRequest();
                request.ServerCertificateName = "CERTIFICATE_NAME";
                var response = iamClient.GetServerCertificate(request);
                Console.WriteLine("CertificateName = " + response.ServerCertificate.ServerCertificateMetadata.ServerCertificateName);
                Console.WriteLine("Certificate Arn = " + response.ServerCertificate.ServerCertificateMetadata.Arn);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static void UpdateCertificate()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new UpdateServerCertificateRequest();
                request.ServerCertificateName = "CERTIFICATE_NAME";
                request.NewServerCertificateName = "NEW_Certificate_NAME";
                var response = iamClient.UpdateServerCertificate(request);
                if (response.HttpStatusCode.ToString() == "OK")
                    Console.WriteLine("Update succesful");
                else
                    Console.WriteLine("HTTpStatusCode returned = " + response.HttpStatusCode.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }

        }
        public static void DeleteCertificate()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new DeleteServerCertificateRequest();
                request.ServerCertificateName = "CERTIFICATE_NAME";
                var response = iamClient.DeleteServerCertificate(request);
                if (response.HttpStatusCode.ToString() == "OK")
                    Console.WriteLine(request.ServerCertificateName + " deleted");
                else
                    Console.WriteLine("HTTpStatusCode returned = " + response.HttpStatusCode.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static void CreateAccountAlias()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new CreateAccountAliasRequest();
                request.AccountAlias = "my-aws-account-alias-2017";
                var response = iamClient.CreateAccountAlias(request);
                if (response.HttpStatusCode.ToString() == "OK")
                    Console.WriteLine(request.AccountAlias + " created.");
                else
                    Console.WriteLine("HTTpStatusCode returned = " + response.HttpStatusCode.ToString());
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static void ListAccountAliases()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new ListAccountAliasesRequest();
                var response = iamClient.ListAccountAliases(request);
                List<string> aliases = response.AccountAliases;
                foreach (string account in aliases)
                {
                    Console.WriteLine("The account alias is: " + account);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static void DeleteAccountAlias()
        {
            try
            {
                var iamClient = new AmazonIdentityManagementServiceClient();
                var request = new DeleteAccountAliasRequest();
                request.AccountAlias = "my-aws-account-alias-2017";
                var response = iamClient.DeleteAccountAlias(request);
                if (response.HttpStatusCode.ToString() == "OK")
                    Console.WriteLine(request.AccountAlias + " deleted.");
                else
                    Console.WriteLine("HTTpStatusCode returned = " + response.HttpStatusCode.ToString());
            }
            catch (NoSuchEntityException e)
            {
                Console.WriteLine(e.Message);
            }
        }
        public static void CreateKeyPair(AmazonEC2Client ec2Client, string keyPairName, string privateKeyFile)
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
        public static void DeleteKeyPair(AmazonEC2Client ec2Client, KeyPair keyPair)
        {
            try
            {
                // Delete key pair created for sample.
                ec2Client.DeleteKeyPair(new DeleteKeyPairRequest { KeyName = keyPair.KeyName });
            }
            catch (AmazonEC2Exception ex)
            {
                // Check the ErrorCode to see if the key already exists.
                if ("InvalidKeyPair.NotFound" == ex.ErrorCode)
                {
                    Console.WriteLine("The key pair \"{0}\" was not found.", keyPair.KeyName);
                }
                else
                {
                    // The exception was thrown for another reason, so re-throw the exception.
                    throw;
                }
            }
        }
        public static void ListUsersAndGroups()
        {
            var iamClient = new AmazonIdentityManagementServiceClient();
            var requestUsers = new ListUsersRequest();
            var responseUsers = iamClient.ListUsers(requestUsers);

            foreach (var user in responseUsers.Users)
            {
                Console.WriteLine("For user {0}:", user.UserName);
                Console.WriteLine("  In groups:");

                var requestGroups = new ListGroupsForUserRequest
                {
                    UserName = user.UserName
                };
                var responseGroups = iamClient.ListGroupsForUser(requestGroups);

                foreach (var group in responseGroups.Groups)
                {
                    Console.WriteLine("    {0}", group.GroupName);
                }

                Console.WriteLine("  Policies:");

                var requestPolicies = new ListUserPoliciesRequest
                {
                    UserName = user.UserName
                };
                var responsePolicies = iamClient.ListUserPolicies(requestPolicies);

                foreach (var policy in responsePolicies.PolicyNames)
                {
                    Console.WriteLine("    {0}", policy);
                }

                var requestAccessKeys = new ListAccessKeysRequest
                {
                    UserName = user.UserName
                };
                var responseAccessKeys = iamClient.ListAccessKeys(requestAccessKeys);

                Console.WriteLine("  Access keys:");

                foreach (var accessKey in responseAccessKeys.AccessKeyMetadata)
                {
                    Console.WriteLine("    {0}", accessKey.AccessKeyId);
                }
            }
        }
        public static void ListUsers()
        {
            var iamClient = new AmazonIdentityManagementServiceClient();
            var requestUsers = new ListUsersRequest() { MaxItems = 10 };
            var responseUsers = iamClient.ListUsers(requestUsers);

            foreach (var user in responseUsers.Users)
            {
                Console.WriteLine("User " + user.UserName + " Created: " + user.CreateDate.ToShortDateString());
            }

        }
        public static void UpdateUser()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var request = new UpdateUserRequest
            {
                UserName = "DemoUser",
                NewUserName = "NewUser"
            };

            try
            {
                var response = client.UpdateUser(request);

            }
            catch (EntityAlreadyExistsException)
            {
                Console.WriteLine("User 'NewUser' already exists.");
            }
        }
        public static void GetUser()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var request = new GetUserRequest()
            {
                UserName = "DemoUser"
            };

            try
            {
                var response = client.GetUser(request);
                Console.WriteLine("Creation date: " + response.User.CreateDate.ToShortDateString());
                Console.WriteLine("Password last used: " + response.User.PasswordLastUsed.ToShortDateString());
                Console.WriteLine("UserId = " + response.User.UserId);

            }
            catch (NoSuchEntityException)
            {
                Console.WriteLine("User DemoUser' does not exist.");
            }
        }
        public static void DeleteUser()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var request = new DeleteUserRequest()
            {
                UserName = "DemoUser"
            };

            try
            {
                var response = client.DeleteUser(request);

            }
            catch (NoSuchEntityException)
            {
                Console.WriteLine("User DemoUser' does not exist.");
            }
        }
        public static void CreatePolicy()
        {
            var client = new AmazonIdentityManagementServiceClient();
            // GenerateRolePolicyDocument() is a custom method.
            string policyDoc = GenerateRolePolicyDocument();

            var request = new CreatePolicyRequest
            {
                PolicyName = "DemoEC2Permissions",
                PolicyDocument = policyDoc
            };

            try
            {
                var createPolicyResponse = client.CreatePolicy(request);
                Console.WriteLine("Make a note, Policy named " + createPolicyResponse.Policy.PolicyName +
                    " has Arn: : " + createPolicyResponse.Policy.Arn);
            }
            catch (EntityAlreadyExistsException)
            {
                Console.WriteLine
                  ("Policy 'DemoEC2Permissions' already exits.");
            }

        }
        public static string GenerateRolePolicyDocument()
        {
            // Create a policy that looks like this:
            /*
            {
              "Version" : "2012-10-17",
              "Id"  : "DemoEC2Permissions",
              "Statement" : [
                {
                  "Sid" : "DemoEC2PermissionsStatement",
                  "Effect" : "Allow",
                  "Action" : [
                    "s3:Get*",
                    "s3:List*"
                  ],
                  "Resource" : "*"
                }
              ]
            }
            */

            var actionGet = new ActionIdentifier("s3:Get*");
            var actionList = new ActionIdentifier("s3:List*");
            var actions = new List<ActionIdentifier>();

            actions.Add(actionGet);
            actions.Add(actionList);

            var resource = new Resource("*");
            var resources = new List<Resource>();

            resources.Add(resource);

            var statement = new Amazon.Auth.AccessControlPolicy.Statement(Amazon.Auth.AccessControlPolicy.Statement.StatementEffect.Allow)
            {
                Actions = actions,
                Id = "DemoEC2PermissionsStatement",
                Resources = resources
            };
            var statements = new List<Amazon.Auth.AccessControlPolicy.Statement>();

            statements.Add(statement);

            var policy = new Policy
            {
                Id = "DemoEC2Permissions",
                Version = "2012-10-17",
                Statements = statements
            };

            return policy.ToJson();
        }
        public static void GetPolicy()
        {
            var client = new AmazonIdentityManagementServiceClient();
            var request = new GetPolicyRequest
            {
                PolicyArn = "arn:aws:iam::123456789:policy/DemoEC2Permissions"
            };

            try
            {
                var response = client.GetPolicy(request);
                Console.WriteLine("Policy " + response.Policy.PolicyName + "successfully retrieved");

            }
            catch (NoSuchEntityException)
            {
                Console.WriteLine
                  ("Policy 'DemoEC2Permissions' does not exist.");
            }

        }
        public static void AttachRolePolicy()
        {
            var client = new AmazonIdentityManagementServiceClient();
            string policy = GenerateRolePolicyDocument();
            CreateRoleRequest roleRequest = new CreateRoleRequest()
            {
                RoleName = "tester",
                AssumeRolePolicyDocument = policy
            };

            var request = new AttachRolePolicyRequest()
            {
                PolicyArn = "arn:aws:iam::123456789:policy/DemoEC2Permissions",
                RoleName = "tester"
            };
            try
            {
                var response = client.AttachRolePolicy(request);
                Console.WriteLine("Policy DemoEC2Permissions attached to Role TestUser");
            }
            catch (NoSuchEntityException)
            {
                Console.WriteLine
                  ("Policy 'DemoEC2Permissions' does not exist");
            }
            catch (InvalidInputException)
            {
                Console.WriteLine
                  ("One of the parameters is incorrect");
            }
        }
        public static void DetachRolePolicy()
        {
            var client = new AmazonIdentityManagementServiceClient();
            string policy = GenerateRolePolicyDocument();
            CreateRoleRequest roleRequest = new CreateRoleRequest()
            {
                RoleName = "tester",
                AssumeRolePolicyDocument = policy
            };

            var request = new DetachRolePolicyRequest()
            {
                PolicyArn = "arn:aws:iam::123456789:policy/DemoEC2Permissions",
                RoleName = "tester"
            };
            try
            {
                var response = client.DetachRolePolicy(request);
                Console.WriteLine("Policy DemoEC2Permissions detached from Role 'tester'");
            }
            catch (NoSuchEntityException e)
            {
                Console.WriteLine
                  (e.Message);
            }
            catch (InvalidInputException i)
            {
                Console.WriteLine
                  (i.Message);
            }
        }



    }
}
