// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ServiceActions.Tests
{
    using Amazon.Glacier;
    using Microsoft.Extensions.Configuration;

    public class GlacierWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonGlacier _amazonGlacierClient;
        private readonly GlacierWrapper _glacierWrapper;

        private readonly string? _archiveName;
        private readonly string? _vaultName;
        private readonly string? _downloadFilePath;
        private readonly string? _tagValue;
        private readonly string? _uploadFilePath;

        private static string? _archiveId;

        public GlacierWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _archiveName = _configuration["ArchiveName"];
            _vaultName = _configuration["VaultName"];
            _downloadFilePath = _configuration["DownloadFilePath"];
            _tagValue = _configuration["TagValue"];
            _uploadFilePath = _configuration["UploadFilePath"];

            _amazonGlacierClient = new AmazonGlacierClient();
            _glacierWrapper = new GlacierWrapper(_amazonGlacierClient);
        }

        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateVaultAsyncTest()
        {
            var success = await _glacierWrapper.CreateVaultAsync(_vaultName);
            Assert.True(success);
        }

        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task UploadArchiveWithArchiveManagerTest()
        {
            _archiveId = await _glacierWrapper.UploadArchiveWithArchiveManager(_vaultName, "Upload stuff for tests.", _archiveId);
            Assert.False(string.IsNullOrEmpty(_archiveId));
        }

        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task AddTagsToVaultAsyncTest()
        {
            var success = await _glacierWrapper.AddTagsToVaultAsync(_vaultName, _archiveName, _tagValue);
            Assert.True(success);
        }

        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task ListVaultsAsyncTest()
        {
            var vaultList = await _glacierWrapper.ListVaultsAsync();
            Assert.NotNull(vaultList);
        }

        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task ListTagsForVaultAsyncTest()
        {
            var tagList = await _glacierWrapper.ListTagsForVaultAsync(_vaultName);
            Assert.NotNull(tagList);
        }

        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task ListJobsAsyncTest()
        {
            var jobList = await _glacierWrapper.ListJobsAsync(_vaultName);
            Assert.NotNull(jobList);
        }

        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task DescribeVaultAsyncTest()
        {
            var vaultArn = await _glacierWrapper.DescribeVaultAsync(_vaultName);
            Assert.NotNull(vaultArn);
        }

        [Fact()]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task DownloadArchiveWithArchiveManagerAsyncTest()
        {
            var success = await _glacierWrapper.DownloadArchiveWithArchiveManagerAsync(_vaultName, _archiveId, _downloadFilePath);
            Assert.True(success);
        }
    }
}