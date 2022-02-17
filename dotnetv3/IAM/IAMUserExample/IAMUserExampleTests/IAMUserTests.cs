// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAMUserExampleTests
{
    using System;
    using System.Collections.Generic;
    using System.Threading;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;
    using Amazon.S3;
    using Amazon.S3.Model;
    using Moq;
    using Xunit;

    /// <summary>
    /// This examples shows how to use the AWS Identity and Access Management
    /// (IAM) service to create and manage users and groups. The example was
    /// created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class IAMUserTests
    {
        private const string GroupName = "TestGroupName";
        private const string PolicyName = "S3ReadOnlyAccess";
        private const string S3ReadOnlyPolicy = "{" +
            "	\"Statement\" : [{" +
                "	\"Action\" : [\"s3:*\"]," +
                "	\"Effect\" : \"Allow\"," +
                "	\"Resource\" : \"*\"" +
            "}]" +
        "}";
        private const string UserName = "S3ReadOnlyUser";
        private const string AccessKeyId = "AKIAIOSFODNN7EXAMPLE";
        string BucketName = "bucket-to-delete";

        [Fact]
        public async Task CreateGroupAsyncTest()
        {
            var mockIAMClient = new Mock<AmazonIdentityManagementServiceClient>();
            mockIAMClient.Setup(client => client.CreateGroupAsync(
                It.IsAny<CreateGroupRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<CreateGroupRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.GroupName))
                {
                    Assert.Equal(request.GroupName, GroupName);
                }
            }).Returns((CreateGroupRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new CreateGroupResponse()
                {
                    Group = new Group
                    {
                        GroupName = GroupName,
                    },
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var client = mockIAMClient.Object;

            var request = new CreateGroupRequest
            {
                GroupName = GroupName,
            };

            var response = await client.CreateGroupAsync(request);

            var ok = (response is not null) &&
                (response.HttpStatusCode == System.Net.HttpStatusCode.OK) &&
                (response.Group.GroupName == GroupName);
            Assert.True(ok, "Could not create group.");
        }

        [Fact]
        public async Task PutGroupPolicyAsyncTest()
        {
            var mockIAMClient = new Mock<AmazonIdentityManagementServiceClient>();
            mockIAMClient.Setup(client => client.PutGroupPolicyAsync(
                It.IsAny<PutGroupPolicyRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<PutGroupPolicyRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.GroupName))
                {
                    Assert.Equal(request.GroupName, GroupName);
                }
            }).Returns((PutGroupPolicyRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PutGroupPolicyResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var client = mockIAMClient.Object;
            var request = new PutGroupPolicyRequest
            {
                GroupName = GroupName,
                PolicyName = PolicyName,
                PolicyDocument = S3ReadOnlyPolicy,
            };

            var response = await client.PutGroupPolicyAsync(request);
            var ok = response.HttpStatusCode == System.Net.HttpStatusCode.OK;

            Assert.True(ok, "Failed to add group policy.");
        }

        [Fact]
        public async Task CreateUserAsyncTest()
        {
            var mockIAMClient = new Mock<AmazonIdentityManagementServiceClient>();
            mockIAMClient.Setup(client => client.CreateUserAsync(
                It.IsAny<CreateUserRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<CreateUserRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.UserName))
                {
                    Assert.Equal(request.UserName, UserName);
                }
            }).Returns((CreateUserRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new CreateUserResponse()
                {
                    User = new User
                    {
                        UserName = UserName,
                    },
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var client = mockIAMClient.Object;

            var request = new CreateUserRequest
            {
                UserName = UserName,
            };

            var response = await client.CreateUserAsync(request);
            var ok = (response.HttpStatusCode == System.Net.HttpStatusCode.OK) && (response.User.UserName == UserName);
            Assert.True(ok, "Was not able to create user.");
        }

        [Fact]
        public async Task AddUserToGroupAsyncTest()
        {
            var mockIAMClient = new Mock<AmazonIdentityManagementServiceClient>();
            mockIAMClient.Setup(client => client.AddUserToGroupAsync(
                It.IsAny<AddUserToGroupRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<AddUserToGroupRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.GroupName))
                {
                    Assert.Equal(request.GroupName, GroupName);
                }
;
                if (!string.IsNullOrEmpty(request.UserName))
                {
                    Assert.Equal(request.UserName, UserName);
                }

            }).Returns((AddUserToGroupRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new AddUserToGroupResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var client = mockIAMClient.Object;
            var request = new AddUserToGroupRequest
            {
                GroupName = GroupName,
                UserName = UserName,
            };

            var response = await client.AddUserToGroupAsync(request);
            var ok = response.HttpStatusCode == System.Net.HttpStatusCode.OK;

            Assert.True(ok, "Was not able to add the user to the group.");
        }

        [Fact]
        public async Task CreateAccessKeyAsyncTest()
        {
            var mockIAMClient = new Mock<AmazonIdentityManagementServiceClient>();
            mockIAMClient.Setup(client => client.CreateAccessKeyAsync(
                It.IsAny<CreateAccessKeyRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<CreateAccessKeyRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.UserName))
                {
                    Assert.Equal(request.UserName, UserName);
                }
            }).Returns((CreateAccessKeyRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new CreateAccessKeyResponse()
                {
                    AccessKey = new AccessKey
                    {
                        UserName = UserName,
                    },
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var client = mockIAMClient.Object;
            var request = new CreateAccessKeyRequest
            {
                UserName = UserName,
            };

            var response = await client.CreateAccessKeyAsync(request);

            var ok = (response.AccessKey.UserName == UserName) && (response.HttpStatusCode == System.Net.HttpStatusCode.OK);
            Assert.True(ok, "Could not create AccessKey.");
        }

        [Fact]
        public async Task ListBucketsAsyncTest()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.ListBucketsAsync(
                It.IsAny<CancellationToken>()
            )).Returns((CancellationToken token) =>
            {
                return Task.FromResult(new ListBucketsResponse()
                {
                    Buckets = new List<S3Bucket>
                    {
                        new S3Bucket
                        {
                            BucketName = "doc-example-bucket",
                            CreationDate = DateTime.Now,
                        },
                        new S3Bucket
                        {
                            BucketName = "doc-example-bucket1",
                            CreationDate = DateTime.Now,
                        },
                        new S3Bucket
                        {
                            BucketName = "doc-example-bucket2",
                            CreationDate = DateTime.Now,
                        },
                    },
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var client = mockS3Client.Object;

            var response = await client.ListBucketsAsync();

            var gotResponse = response is not null;

            if (gotResponse)
            {
                var ok = (response.HttpStatusCode == System.Net.HttpStatusCode.OK) && (response.Buckets.Count == 3);
                Assert.True(ok, "Couldn't get list of buckets.");
            }
        }

        [Fact]
        public async Task CleanUpResourcesTest()
        {
            var mockIAMClient = new Mock<AmazonIdentityManagementServiceClient>();
            var mockS3Client = new Mock<AmazonS3Client>();

            mockIAMClient.Setup(client => client.RemoveUserFromGroupAsync(
                It.IsAny<RemoveUserFromGroupRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<RemoveUserFromGroupRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.GroupName))
                {
                    Assert.Equal(request.GroupName, GroupName);
                }
            }).Returns((RemoveUserFromGroupRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new RemoveUserFromGroupResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            mockIAMClient.Setup(client => client.DeleteAccessKeyAsync(
                It.IsAny<DeleteAccessKeyRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteAccessKeyRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.UserName))
                {
                    Assert.Equal(request.UserName, UserName);
                }
                if (!string.IsNullOrEmpty(request.AccessKeyId))
                {
                    Assert.Equal(request.AccessKeyId, AccessKeyId);
                }
            }).Returns((DeleteAccessKeyRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteAccessKeyResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            mockIAMClient.Setup(client => client.DeleteUserAsync(
                It.IsAny<DeleteUserRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteUserRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.UserName))
                {
                    Assert.Equal(request.UserName, UserName);
                }
            }).Returns((DeleteUserRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteUserResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            mockIAMClient.Setup(client => client.DeleteGroupPolicyAsync(
                It.IsAny<DeleteGroupPolicyRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteGroupPolicyRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.GroupName))
                {
                    Assert.Equal(request.GroupName, GroupName);
                }
            }).Returns((DeleteGroupPolicyRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteGroupPolicyResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            mockIAMClient.Setup(client => client.DeleteGroupAsync(
                It.IsAny<DeleteGroupRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteGroupRequest, CancellationToken>((request, token) =>
            {
                if (!string.IsNullOrEmpty(request.GroupName))
                {
                    Assert.Equal(request.GroupName, GroupName);
                }
            }).Returns((DeleteGroupRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteGroupResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var iamClient = mockIAMClient.Object;

            mockS3Client.Setup(client => client.DeleteBucketAsync(
                It.IsAny<string>(),
                It.IsAny<CancellationToken>()
            )).Callback<string, CancellationToken>((bucketName, token) =>
            {
                if (!string.IsNullOrEmpty(bucketName))
                {
                    Assert.Equal(bucketName, BucketName);
                }
            }).Returns((string bucketName, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteBucketResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            var s3Client = mockS3Client.Object;

            await IAMUserExample.IAMUser.CleanUpResources(iamClient, s3Client, UserName, GroupName, BucketName, AccessKeyId);
        }
    }
}
