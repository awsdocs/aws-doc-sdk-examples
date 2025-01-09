﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Xunit.Extensions.Ordering;

namespace TransferUtilityBasics.Tests
{
    public class TransferMethodsTests
    {
        private readonly IConfiguration _configuration;

        readonly IAmazonS3 _client;
        readonly TransferUtility transferUtil;
        private readonly string _localPath = $".\\TransferFolderTest";

        public TransferMethodsTests()
        {
            _client = new AmazonS3Client();
            transferUtil = new TransferUtility(_client);

            _client = new AmazonS3Client();
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from JSON file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();
        }

        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task DownloadSingleFileAsyncTest()
        {
            var keyName = _configuration["FileToDownload"];
            if (File.Exists(keyName))
            {
                File.Delete(keyName);
            }

            var success = await TransferMethods.DownloadSingleFileAsync(
                transferUtil,
                _configuration["BucketName"],
                keyName,
                _localPath);

            Assert.True(success, $"Couldn't download {keyName}.");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public async Task DownloadS3DirectoryAsyncTest()
        {
            var downloadPath = $"{_localPath}\\TestDownloadFolder";
            var s3Path = _configuration["S3Path"];
            Directory.CreateDirectory(downloadPath);
            var success = await TransferMethods.DownloadS3DirectoryAsync(
                transferUtil,
                _configuration["BucketName"],
                s3Path,
                downloadPath
                );

            Assert.True(success, $"Couldn't download files from {s3Path}.");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public async Task UploadSingleFileAsyncTest()
        {
            var fileName = _configuration["FileToUpload"];
            var bucketName = _configuration["BucketName"];

            var success = await TransferMethods.UploadSingleFileAsync(
                transferUtil,
                bucketName,
                fileName,
                _localPath);

            Assert.True(success, $"Couldn't upload files to {bucketName}");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public async Task UploadFullDirectoryAsyncTest()
        {
            var bucketName = _configuration["BucketName"];
            var keyPrefix = "UploadFolder";
            var uploadPath = $"{_localPath}\\UploadFolder";

            var success = await TransferMethods.UploadFullDirectoryAsync(
                transferUtil,
                bucketName,
                keyPrefix,
                uploadPath);

            Assert.True(success, $"Couldn't upload {uploadPath} to {bucketName}");
        }
    }
}