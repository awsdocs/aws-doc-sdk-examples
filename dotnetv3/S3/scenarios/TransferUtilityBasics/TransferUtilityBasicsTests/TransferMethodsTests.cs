using Microsoft.VisualStudio.TestTools.UnitTesting;
using Amazon;
using Amazon.S3;
using Amazon.S3.Transfer;
using TransferUtilityBasics;

namespace TransferUtilityBasics.Tests
{
    [TestClass()]
    public class TransferMethodsTests
    {
        IAmazonS3 client;
        TransferUtility transferUtil;
        string LocalPath = $"{Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData)}\\TransferFolderTest";
        string bucketName;

        TransferMethodsTests()
        {
            client = new AmazonS3Client();
            transferUtil = new TransferUtility();
            bucketName = "doc-example-bucket1-test";
        }

        [TestMethod()]
        public async Task DownloadSingleFileAsyncTest()
        {
            var keyName = "FileToDownload.docx";
            var success = await TransferMethods.DownloadSingleFileAsync(transferUtil, bucketName, keyName, LocalPath);
            Assert.IsTrue(success, $"Couldn't download {keyName}.");
        }

        [TestMethod()]
        public async Task DownloadS3DirectoryAsyncTest()
        {
            var downloadPath = $"{LocalPath}\\TestDownloadFolder";
            var s3Path = "DownloadTest";

            var success = await TransferMethods.DownloadS3DirectoryAsync(transferUtil, bucketName, downloadPath, s3Path);
            Assert.IsTrue(success, "Couldn't download files from {s3Path}.");
        }

        [TestMethod()]
        public async Task UploadSingleFileAsyncTest()
        {
            var fileName = "UploadTest.docx";

            var success = await TransferMethods.UploadSingleFileAsync(
                transferUtil,
                bucketName,
                fileName,
                LocalPath);
            Assert.IsTrue(success, $"Couldn't upload files to {bucketName}");
        }

        [TestMethod()]
        public async Task UploadFullDirectoryAsyncTest()
        {
            var keyPrefix = "UploadFolder";
            var uploadPath = $"{LocalPath}\\UploadFolder";

            var success = await TransferMethods.UploadFullDirectoryAsync(transferUtil, bucketName, keyPrefix, uploadPath);
            Assert.IsTrue(success, $"Couldn't upload {uploadPath} to {bucketName}");
        }
    }
}