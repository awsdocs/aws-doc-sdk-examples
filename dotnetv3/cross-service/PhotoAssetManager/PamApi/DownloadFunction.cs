// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.Lambda.Core;
using Amazon.S3;
using Amazon.SimpleNotificationService;
using Amazon.Util;
using AWS.Lambda.Powertools.Logging;
using PamServices;

namespace PamApi;

/// <summary>
/// The function code for downloading and zipping
/// images from a list of labels.
/// </summary>
public class DownloadFunction
{
    IAmazonS3 S3Client { get; set; }
    private readonly StorageService _storageService;
    private readonly NotificationService _notificationService;
    private readonly LabelService _labelService;

    /// <summary>
    /// Default constructor. This constructor is used by AWS Lambda to construct the instance. When invoked in a Lambda environment,
    /// the AWS credentials will come from the AWS Identity and Access Management (IAM) role associated with the function. The AWS Region will be set to the
    /// Region the Lambda function is executed in.
    /// </summary>
    public DownloadFunction()
    {
        S3Client = new AmazonS3Client();
        _notificationService = new NotificationService(new AmazonSimpleNotificationServiceClient());
        _storageService = new StorageService(S3Client);
        _labelService = new LabelService(new DynamoDBContext(new AmazonDynamoDBClient()));
        var labelsTableName = Environment.GetEnvironmentVariable("LABELS_TABLE_NAME");

        if (labelsTableName != null)
        {
            AWSConfigsDynamoDB.Context.AddMapping(new TypeMapping(typeof(Label), labelsTableName));
        }
    }

    /// <summary>
    /// Constructs an instance with a preconfigured S3 client. This can be used for testing the outside of the Lambda environment.
    /// </summary>
    /// <param name="s3Client"></param>
    public DownloadFunction(IAmazonS3 s3Client)
    {
        this.S3Client = s3Client;
        _notificationService = new NotificationService(new AmazonSimpleNotificationServiceClient());
        _storageService = new StorageService(S3Client);
        _labelService = new LabelService(new DynamoDBContext(new AmazonDynamoDBClient()));
    }

    /// <summary>
    /// Starts the download and zip operation.
    /// </summary>
    /// <param name="input">The string input.</param>
    /// <param name="context">The Lambda context.</param>
    /// <returns>Async task.</returns>
    public async Task FunctionHandler(DownloadRequest request, ILambdaContext context)
    {
        var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");
        var workingBucketName = Environment.GetEnvironmentVariable("WORKING_BUCKET_NAME");
        var topicArn = Environment.GetEnvironmentVariable("NOTIFICATION_TOPIC");

        try
        {
            Logger.LogInformation($"Starting download and zip operation.", request);
            var labelsList = request.labels.ToList();
            var imageKeys = await _labelService.GetAllImagesForLabels(labelsList);
            var zipArchiveUrl =
                await _storageService.GenerateZipFromImages(imageKeys, storageBucketName!,
                    workingBucketName!);
            await _notificationService.SendNotification(topicArn!, "Image download",
                $"Your images are available here: {zipArchiveUrl}");
            Logger.LogInformation($"url: {zipArchiveUrl}");
        }
        catch (Exception e)
        {
            Logger.LogError(e, $"Error starting download.");
            throw;
        }
    }
}