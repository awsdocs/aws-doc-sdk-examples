// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// The Amazon S3 client here uses the default user credentials
// defined for this computer.
IAmazonS3 client = new AmazonS3Client();
var transferUtil = new TransferUtility(client);

// Change the following values to Amazon S3 buckets that
// exist in your Amazon account.Make sure that you have an
// existing bucket that you can use or create a new bucket on
// your account.
var bucketName = "igsmith-doc-example-bucket1";
var localPath = $"{Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData)}\\TransferFolder";

DisplayInstructions();

PressEnter();

// Upload a single file to an Amazon S3 bucket.
var fileToUpload = "UploadTest.docx";

Console.WriteLine($"Uploading {fileToUpload} to the Amazon S3 bucket, {bucketName}.");

var success = await TransferMethods.UploadSingleFileAsync(transferUtil, bucketName, fileToUpload, localPath);
if (success)
{
    Console.WriteLine($"Successfully uploaded the file, {fileToUpload} to {bucketName}.");
}

PressEnter();

// Upload a local directory to an Amazon S3 bucket.
var keyPrefix = "UploadFolder";
var uploadPath = $"{localPath}\\UploadFolder";

Console.WriteLine($"Uploading the files in {uploadPath} to {bucketName}");

success = await TransferMethods.UploadFullDirectoryAsync(transferUtil, bucketName, keyPrefix, uploadPath);
if (success)
{
    Console.WriteLine($"Successfully uploaded the files in {uploadPath} to {bucketName}.");
}

PressEnter();


// Download a single file from an Amazon S3 bucket.
var keyName = "FileToDownload.docx";

Console.WriteLine($"Downloading {keyName} from {bucketName}.");

success = await TransferMethods.DownloadSingleFileAsync(transferUtil, bucketName, keyName, localPath);
if (success)
{
    Console.WriteLine("$Successfully downloaded the file, {keyName} from {bucketName}.");
}

PressEnter();

// Download the contents of a directory from an Amazon S3 bucket.
var s3Path = "DownloadFolder";
var downloadPath = $"{localPath}\\DownloadFolder";

Console.WriteLine("Downloading the contents of {bucketName}\\{s3Path}");

success = await TransferMethods.DownloadS3DirectoryAsync(transferUtil, bucketName, s3Path, downloadPath);
if (success)
{
    Console.WriteLine($"Downloaded the files in {bucketName} to {downloadPath}.");
}

Console.WriteLine("The TransferUtility Basics application has completed.");

static void DisplayInstructions()
{
    var sepBar = new string('-', 80);

    Console.Clear();
    Console.WriteLine(sepBar);
    Console.WriteLine(CenterText("Amazon S3 Transfer Utility Basics"));
    Console.WriteLine(sepBar);
    Console.WriteLine("This program shows how to use the Amazon S3 Transfer Utility.");
    Console.WriteLine(sepBar);
    Console.WriteLine("It performs the following actions:");
    Console.WriteLine("\t1. Upload a single object to an Amazon S3 bucket.");
    Console.WriteLine("\t2. Upload all an entire directory from the local computer to an Amazon\n\t   S3 bucket.");
    Console.WriteLine("\t3. Download a single object from an Amazon S3 bucket.");
    Console.WriteLine("\t4. Download the objects in an Amazon S3 directory to a local directory.");
    Console.WriteLine($"\n{sepBar}");
}

static void PressEnter()
{
    Console.WriteLine("Press <Enter> to continue.");
    _ = Console.ReadLine();
    Console.WriteLine("\n");
}

static string CenterText(string textToCenter)
{
    var centeredText = new StringBuilder();
    centeredText.Append(new string(' ', (int)(80 - textToCenter.Length) / 2));
    centeredText.Append(textToCenter);
    return centeredText.ToString();
}
